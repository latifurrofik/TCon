package com.tcon.companion.core

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class TConUpdate(
    val version: String,
    val notes: String,
    val apkUrl: String,
    val apkName: String
)

/**
 * Checks the official GitHub Releases endpoint and downloads a release APK.
 * Android Package Installer remains responsible for signature verification and installation.
 */
class InAppUpdater(private val context: Context) {
    companion object {
        private const val RELEASE_API =
            "https://api.github.com/repos/latifurrofik/TCon/releases/latest"
        private const val PREFS = "tcon_updater"
        private const val KEY_DOWNLOAD_ID = "download_id"
        private const val KEY_PENDING_INSTALL = "pending_install_path"
        private const val FILE_PROVIDER_AUTHORITY = "com.tcon.companion.fileprovider"
    }

    suspend fun checkLatest(): TConUpdate? = withContext(Dispatchers.IO) {
        val connection = (URL(RELEASE_API).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            setRequestProperty("User-Agent", "TCon-Android-Updater")
        }
        try {
            val status = connection.responseCode
            if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                error("Belum ada rilis stabil TCon di GitHub. Pembaruan akan tersedia setelah APK release pertama dipublikasikan.")
            }
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream.bufferedReader().use { it.readText() }
            require(status in 200..299) { "GitHub merespons HTTP $status." }
            val release = JSONObject(body)
            require(!release.optBoolean("draft") && !release.optBoolean("prerelease")) {
                "Rilis terbaru belum stabil atau masih draft."
            }
            val tag = release.optString("tag_name").removePrefix("v").trim()
            require(tag.isNotBlank()) { "Tag versi rilis tidak ditemukan." }
            val current = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"
            if (compareVersions(tag, current) <= 0) return@withContext null

            val assets = release.optJSONArray("assets") ?: error("Rilis belum memiliki file APK.")
            var apkUrl: String? = null
            var apkName: String? = null
            for (index in 0 until assets.length()) {
                val asset = assets.getJSONObject(index)
                val name = asset.optString("name")
                val url = asset.optString("browser_download_url")
                if (name.endsWith(".apk", ignoreCase = true) &&
                    url.startsWith("https://github.com/latifurrofik/TCon/releases/download/")) {
                    apkName = name
                    apkUrl = url
                    break
                }
            }
            require(!apkUrl.isNullOrBlank() && !apkName.isNullOrBlank()) {
                "Rilis $tag belum menyertakan APK. Pengelola repository perlu mengunggah APK release."
            }
            TConUpdate(tag, release.optString("body"), apkUrl!!, apkName!!)
        } finally {
            connection.disconnect()
        }
    }

    suspend fun downloadAndInstall(activity: Activity, update: TConUpdate) {
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
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request).also {
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().putLong(KEY_DOWNLOAD_ID, it).apply()
            }
        }

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        while (true) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = manager.query(query)
            val (status, reason) = cursor.use {
                require(it.moveToFirst()) { "Unduhan tidak ditemukan." }
                it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) to
                    it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
            }
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> break
                DownloadManager.STATUS_FAILED -> error("Unduhan gagal (kode $reason).")
                DownloadManager.STATUS_PAUSED -> error("Unduhan dijeda (kode $reason).")
            }
            delay(700)
        }

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "TCon-update.apk"
        )
        require(apkFile.exists() && apkFile.length() > 0L) { "File APK hasil unduhan tidak ditemukan." }
        installOrRequestPermission(activity, apkFile)
    }

    fun resumePendingInstall(activity: Activity) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val path = prefs.getString(KEY_PENDING_INSTALL, null) ?: return
        if (!activity.packageManager.canRequestPackageInstalls()) return
        prefs.edit().remove(KEY_PENDING_INSTALL).apply()
        val file = File(path)
        if (file.exists()) launchInstaller(activity, file)
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

    private fun compareVersions(left: String, right: String): Int {
        val a = left.split('.', '-', '+').map { it.toIntOrNull() ?: 0 }
        val b = right.split('.', '-', '+').map { it.toIntOrNull() ?: 0 }
        for (index in 0 until maxOf(a.size, b.size)) {
            val diff = (a.getOrElse(index) { 0 }).compareTo(b.getOrElse(index) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }
}
