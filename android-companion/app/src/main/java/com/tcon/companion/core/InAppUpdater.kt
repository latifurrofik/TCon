package com.tcon.companion.core

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

data class TConUpdate(
    val version: String,
    val notes: String,
    val apkUrl: String,
    val apkName: String,
    val expectedSize: Long,
    val sha256: String
)

/**
 * Checks the public GitHub Releases API and verifies the downloaded APK digest.
 * Android Package Installer performs the final package/signature validation.
 */
class InAppUpdater(private val context: Context) {
    companion object {
        private const val RELEASE_API =
            "https://api.github.com/repos/latifurrofik/TCon/releases/latest"
        private const val PREFS = "tcon_updater"
        private const val KEY_DOWNLOAD_ID = "download_id"
        private const val KEY_PENDING_INSTALL = "pending_install_path"
        private const val FILE_PROVIDER_AUTHORITY = "com.tcon.companion.fileprovider"
        private const val MAX_APK_BYTES = 150L * 1024L * 1024L
        private const val DOWNLOAD_TIMEOUT_MS = 15L * 60L * 1000L
        private val VERSION_PATTERN = Regex("\\d+\\.\\d+\\.\\d+")
    }

    suspend fun checkLatest(): TConUpdate? = withContext(Dispatchers.IO) {
        val connection = (URL(RELEASE_API).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2026-03-10")
            setRequestProperty("User-Agent", "TCon-Android-Updater")
        }
        try {
            val status = connection.responseCode
            if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                error("Belum ada rilis stabil TCon di GitHub. Pembaruan tersedia setelah APK release pertama dipublikasikan.")
            }
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            require(status in 200..299) { "GitHub merespons HTTP $status." }
            val release = JSONObject(body)
            require(!release.optBoolean("draft") && !release.optBoolean("prerelease")) {
                "Rilis terbaru belum stabil atau masih draft."
            }

            val tag = release.optString("tag_name").removePrefix("v").trim()
            require(VERSION_PATTERN.matches(tag)) { "Tag versi rilis tidak valid." }
            val current = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"
            if (compareVersions(tag, current) <= 0) return@withContext null

            val assets = release.optJSONArray("assets") ?: error("Rilis belum memiliki file APK.")
            var selected: JSONObject? = null
            for (index in 0 until assets.length()) {
                val asset = assets.getJSONObject(index)
                val name = asset.optString("name")
                val url = asset.optString("browser_download_url")
                if (name.endsWith(".apk", ignoreCase = true) &&
                    url.startsWith("https://github.com/latifurrofik/TCon/releases/download/")) {
                    selected = asset
                    break
                }
            }
            val asset = selected ?: error("Rilis $tag belum menyertakan APK.")
            val size = asset.optLong("size", -1L)
            require(size > 0L && size <= MAX_APK_BYTES) { "Ukuran APK tidak valid atau melebihi batas 150 MB." }
            val digest = asset.optString("digest")
            require(digest.matches(Regex("sha256:[a-fA-F0-9]{64}"))) {
                "Rilis tidak menyediakan SHA-256 APK. Unduhan dibatalkan demi keamanan."
            }

            TConUpdate(
                version = tag,
                notes = release.optString("body"),
                apkUrl = asset.getString("browser_download_url"),
                apkName = asset.getString("name"),
                expectedSize = size,
                sha256 = digest.substringAfter(':').lowercase()
            )
        } finally {
            connection.disconnect()
        }
    }

    suspend fun downloadAndInstall(activity: Activity, update: TConUpdate) {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val previousId = prefs.getLong(KEY_DOWNLOAD_ID, -1L)
        if (previousId >= 0L) manager.remove(previousId)

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "TCon-update.apk"
        )
        apkFile.parentFile?.mkdirs()
        if (apkFile.exists()) apkFile.delete()

        val downloadId = withContext(Dispatchers.IO) {
            val request = DownloadManager.Request(Uri.parse(update.apkUrl))
                .setTitle("Memperbarui TCon")
                .setDescription("Mengunduh TCon ${update.version}")
                .setMimeType("application/vnd.android.package-archive")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalFilesDir(
                    context, Environment.DIRECTORY_DOWNLOADS, "TCon-update.apk"
                )
            manager.enqueue(request).also {
                prefs.edit().putLong(KEY_DOWNLOAD_ID, it).apply()
            }
        }

        try {
            val deadline = SystemClock.elapsedRealtime() + DOWNLOAD_TIMEOUT_MS
            while (true) {
                check(SystemClock.elapsedRealtime() < deadline) {
                    "Unduhan melewati batas 15 menit. Periksa koneksi lalu coba lagi."
                }
                val cursor = manager.query(DownloadManager.Query().setFilterById(downloadId))
                val (status, reason) = cursor.use {
                    require(it.moveToFirst()) { "Unduhan tidak ditemukan." }
                    it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) to
                        it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                }
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> break
                    DownloadManager.STATUS_FAILED -> error("Unduhan gagal (kode $reason).")
                    // A paused DownloadManager task can resume automatically when network returns.
                    DownloadManager.STATUS_PAUSED -> Unit
                }
                delay(700)
            }

            require(apkFile.exists() && apkFile.isFile) { "File APK hasil unduhan tidak ditemukan." }
            require(apkFile.length() == update.expectedSize) {
                "Ukuran APK tidak cocok dengan metadata GitHub. File ditolak."
            }
            val actualDigest = withContext(Dispatchers.IO) { sha256(apkFile) }
            require(actualDigest == update.sha256) {
                "Verifikasi SHA-256 gagal. APK tidak akan dipasang."
            }
            prefs.edit().remove(KEY_DOWNLOAD_ID).apply()
            installOrRequestPermission(activity, apkFile)
        } catch (error: Exception) {
            manager.remove(downloadId)
            prefs.edit().remove(KEY_DOWNLOAD_ID).apply()
            apkFile.delete()
            throw error
        }
    }

    fun resumePendingInstall(activity: Activity) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val path = prefs.getString(KEY_PENDING_INSTALL, null) ?: return
        if (!activity.packageManager.canRequestPackageInstalls()) return
        val file = File(path)
        if (!file.exists() || !file.isFile || file.length() <= 0L) {
            prefs.edit().remove(KEY_PENDING_INSTALL).apply()
            return
        }
        launchInstaller(activity, file)
        prefs.edit().remove(KEY_PENDING_INSTALL).apply()
    }

    private fun installOrRequestPermission(activity: Activity, apkFile: File) {
        if (activity.packageManager.canRequestPackageInstalls()) {
            launchInstaller(activity, apkFile)
            return
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_PENDING_INSTALL, apkFile.absolutePath).apply()
        val intent = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
        )
        activity.startActivity(intent)
    }

    private fun launchInstaller(activity: Activity, apkFile: File) {
        val contentUri = FileProvider.getUriForFile(
            context, FILE_PROVIDER_AUTHORITY, apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        activity.startActivity(intent)
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(32 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }
    }

    private fun compareVersions(left: String, right: String): Int {
        val a = left.split('.', '-', '+').map { it.toIntOrNull() ?: 0 }
        val b = right.split('.', '-', '+').map { it.toIntOrNull() ?: 0 }
        for (index in 0 until maxOf(a.size, b.size)) {
            val diff = a.getOrElse(index) { 0 }.compareTo(b.getOrElse(index) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }

}
