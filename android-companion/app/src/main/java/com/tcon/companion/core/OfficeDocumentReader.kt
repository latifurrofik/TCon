package com.tcon.companion.core

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

/**
 * Small, bounded text extraction for DOCX and XLSX files.
 * This is intentionally not a full Office renderer: charts, embedded media, macros,
 * formulas without cached values, and complex layout are not interpreted.
 */
object OfficeDocumentReader {
    const val MAX_FILE_BYTES = 2 * 1024 * 1024
    const val MAX_EXTRACTED_CHARS = 12_000
    private const val MAX_UNCOMPRESSED_BYTES = 5 * 1024 * 1024

    fun read(contentResolver: ContentResolver, uri: Uri): TextAttachment {
        val name = queryName(contentResolver, uri)
        val extension = name.substringAfterLast('.', "").lowercase()
        require(extension == "docx" || extension == "xlsx") {
            "Format Office belum didukung: $name. Gunakan DOCX atau XLSX."
        }
        val bytes = contentResolver.openInputStream(uri)?.use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8 * 1024)
            var total = 0
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                total += count
                require(total <= MAX_FILE_BYTES) { "File $name melebihi batas 2 MB." }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        } ?: error("File $name tidak dapat dibuka.")
        require(bytes.isNotEmpty()) { "File $name kosong." }
        val extracted = when (extension) {
            "docx" -> extractDocx(bytes)
            "xlsx" -> extractXlsx(bytes)
            else -> ""
        }.take(MAX_EXTRACTED_CHARS).trim()
        require(extracted.isNotBlank()) { "Tidak ditemukan teks yang dapat dibaca di $name." }
        return TextAttachment(name, extracted)
    }

    private fun extractDocx(bytes: ByteArray): String {
        val entries = readZipEntries(bytes, setOf("word/document.xml"))
        val document = entries["word/document.xml"] ?: error("Struktur DOCX tidak valid.")
        val parser = Xml.newPullParser()
        parser.setInput(ByteArrayInputStream(document), "UTF-8")
        val result = StringBuilder()
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT && result.length < MAX_EXTRACTED_CHARS) {
            if (event == XmlPullParser.START_TAG && localName(parser) == "t") {
                result.append(parser.nextText()).append(' ')
            } else if (event == XmlPullParser.END_TAG && localName(parser) == "p") {
                result.append('\n')
            }
            event = parser.next()
        }
        return result.toString()
    }

    private fun extractXlsx(bytes: ByteArray): String {
        val entries = readZipEntries(bytes, null)
        val sharedStrings = entries["xl/sharedStrings.xml"]?.let(::parseSharedStrings).orEmpty()
        val sheets = entries.filterKeys { it.startsWith("xl/worksheets/sheet") && it.endsWith(".xml") }
            .toSortedMap()
        val output = StringBuilder()
        sheets.forEach { (sheetName, sheetXml) ->
            if (output.length >= MAX_EXTRACTED_CHARS) return@forEach
            output.append("\n[").append(sheetName.substringAfterLast('/')).append("]\n")
            val parser = Xml.newPullParser()
            parser.setInput(ByteArrayInputStream(sheetXml), "UTF-8")
            var inCell = false
            var cellType = ""
            var cellValue = StringBuilder()
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT && output.length < MAX_EXTRACTED_CHARS) {
                when (event) {
                    XmlPullParser.START_TAG -> when (localName(parser)) {
                        "row" -> output.append('\n')
                        "c" -> {
                            inCell = true
                            cellType = parser.getAttributeValue(null, "t").orEmpty()
                            cellValue = StringBuilder()
                        }
                        "v", "t" -> if (inCell) {
                            val raw = parser.nextText()
                            if (cellType == "s" && localName(parser) == "v") {
                                val index = raw.toIntOrNull()
                                cellValue.append(index?.let { sharedStrings.getOrNull(it) } ?: raw)
                            } else cellValue.append(raw)
                        }
                    }
                    XmlPullParser.END_TAG -> when (localName(parser)) {
                        "c" -> {
                            output.append(cellValue).append(" | ")
                            inCell = false
                        }
                    }
                }
                event = parser.next()
            }
        }
        return output.toString()
    }

    private fun parseSharedStrings(xml: ByteArray): List<String> {
        val parser = Xml.newPullParser()
        parser.setInput(ByteArrayInputStream(xml), "UTF-8")
        val values = mutableListOf<String>()
        var inItem = false
        var current = StringBuilder()
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT && values.size < 100_000) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "si" -> { inItem = true; current = StringBuilder() }
                    "t" -> if (inItem) current.append(parser.nextText())
                }
                XmlPullParser.END_TAG -> if (localName(parser) == "si" && inItem) {
                    values += current.toString()
                    inItem = false
                }
            }
            event = parser.next()
        }
        return values
    }

    private fun readZipEntries(bytes: ByteArray, only: Set<String>?): Map<String, ByteArray> {
        val entries = linkedMapOf<String, ByteArray>()
        var total = 0
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.isDirectory) {
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(8 * 1024)
                    while (true) {
                        val count = zip.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= MAX_UNCOMPRESSED_BYTES) { "Isi Office terlalu besar untuk diproses dengan aman." }
                        output.write(buffer, 0, count)
                    }
                    if (only == null || entry.name in only) entries[entry.name] = output.toByteArray()
                }
                zip.closeEntry()
            }
        }
        return entries
    }

    private fun localName(parser: XmlPullParser): String = parser.name.substringAfter(":")

    private fun queryName(contentResolver: ContentResolver, uri: Uri): String {
        var name: String? = null
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            if (cursor?.moveToFirst() == true) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return name?.takeIf { it.isNotBlank() } ?: "dokumen-office"
    }
}
