package com.tcon.companion.core

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Reads supported binary attachments selected explicitly by the user.
 * Bytes remain in memory and are sent only to the AI providers selected for a meeting.
 */
object BinaryAttachmentReader {
    const val MAX_FILE_BYTES = 2 * 1024 * 1024
    const val MAX_TOTAL_BYTES = 5 * 1024 * 1024

    private val supportedImageTypes = setOf("image/jpeg", "image/png", "image/webp", "image/gif")

    fun read(contentResolver: ContentResolver, uri: Uri): BinaryAttachment {
        val name = queryName(contentResolver, uri)
        val extension = name.substringAfterLast('.', "").lowercase()
        val declaredMime = contentResolver.getType(uri)?.lowercase().orEmpty()
        val mime = when {
            declaredMime == "application/pdf" || extension == "pdf" -> "application/pdf"
            declaredMime in supportedImageTypes -> declaredMime
            extension in setOf("jpg", "jpeg") -> "image/jpeg"
            extension == "png" -> "image/png"
            extension == "webp" -> "image/webp"
            extension == "gif" -> "image/gif"
            else -> error("Format $name belum didukung. Gunakan PDF, JPG, PNG, WEBP, atau GIF.")
        }
        val bytes = contentResolver.openInputStream(uri)?.use { stream ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8 * 1024)
            var total = 0
            while (true) {
                val count = stream.read(buffer)
                if (count < 0) break
                total += count
                require(total <= MAX_FILE_BYTES) { "File $name melebihi batas 2 MB." }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        } ?: error("File $name tidak dapat dibuka.")
        require(bytes.isNotEmpty()) { "File $name kosong." }
        return BinaryAttachment(name, mime, Base64.encodeToString(bytes, Base64.NO_WRAP))
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
        return name?.takeIf { it.isNotBlank() } ?: "lampiran"
    }
}
