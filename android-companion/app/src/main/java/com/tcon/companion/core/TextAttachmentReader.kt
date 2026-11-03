package com.tcon.companion.core

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

data class TextAttachment(
    val name: String,
    val text: String
)

/**
 * Reads small, text-based documents selected by the user through Android's Storage Access Framework.
 * Binary formats (PDF, Office documents, images, audio and video) are deliberately rejected until
 * provider-specific extraction/vision support is implemented.
 */
object TextAttachmentReader {
    const val MAX_FILE_BYTES = 512 * 1024
    const val MAX_FILES = 5
    const val MAX_CONTEXT_CHARS = 12_000

    private val supportedExtensions = setOf(
        "txt", "md", "markdown", "csv", "tsv", "json", "xml", "html", "htm",
        "yaml", "yml", "log"
    )

    fun read(contentResolver: ContentResolver, uri: Uri): TextAttachment {
        val name = queryName(contentResolver, uri)
        val extension = name.substringAfterLast('.', "").lowercase()
        val mime = contentResolver.getType(uri)?.lowercase().orEmpty()
        val isTextMime = mime.startsWith("text/") ||
            mime == "application/json" ||
            mime == "application/xml" ||
            mime.endsWith("+json") ||
            mime.endsWith("+xml")
        require(extension in supportedExtensions || isTextMime) {
            "Format $name belum didukung. Gunakan TXT, MD, CSV, JSON, XML, HTML, YAML, atau LOG."
        }

        val input = contentResolver.openInputStream(uri)
            ?: error("File $name tidak dapat dibuka.")
        val bytes = input.use { stream ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8 * 1024)
            var total = 0
            while (true) {
                val count = stream.read(buffer)
                if (count < 0) break
                total += count
                require(total <= MAX_FILE_BYTES) {
                    "File $name melebihi batas 512 KB."
                }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
        val text = bytes.toString(Charset.forName("UTF-8"))
            .removePrefix("\uFEFF")
            .trim()
        require(text.isNotBlank()) { "File $name kosong atau tidak berisi teks." }
        return TextAttachment(name, text)
    }

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
        return name?.takeIf { it.isNotBlank() } ?: "dokumen.txt"
    }
}
