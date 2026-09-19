package com.f1ndle.tgwsproxy

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionName: String,
    val title: String,
    val changelog: String,
    val releasePageUrl: String,
    val downloadUrl: String,
)

object UpdateManager {
    private const val PRIMARY_REPO = "f1ndles/tg-ws-proxy"
    private const val FALLBACK_REPO = "f1ndles/tgws"

    suspend fun checkUpdate(context: Context): UpdateInfo? = withContext(Dispatchers.IO) {
        val currentVersion = getCurrentVersion(context)
        // Try primary repository first, then fallback
        val release = fetchLatestRelease(PRIMARY_REPO) ?: fetchLatestRelease(FALLBACK_REPO) ?: return@withContext null

        val tagName = release.optString("tag_name", "").trim()
        if (tagName.isEmpty()) return@withContext null

        if (isNewerVersion(tagName, currentVersion)) {
            val title = release.optString("name", tagName)
            val body = release.optString("body", "")
            val htmlUrl = release.optString("html_url", "https://github.com/$PRIMARY_REPO/releases/latest")

            var apkDownloadUrl = htmlUrl
            val assets = release.optJSONArray("assets")
            if (assets != null) {
                var bestCandidate: String? = null
                for (i in 0 until assets.length()) {
                    val asset = assets.optJSONObject(i) ?: continue
                    val name = asset.optString("name", "").lowercase()
                    val url = asset.optString("browser_download_url", "")
                    if (name.endsWith(".apk")) {
                        if (name.contains("arm64") || name.contains("universal")) {
                            bestCandidate = url
                            break
                        } else if (bestCandidate == null) {
                            bestCandidate = url
                        }
                    }
                }
                if (bestCandidate != null) {
                    apkDownloadUrl = bestCandidate
                }
            }

            return@withContext UpdateInfo(
                versionName = tagName,
                title = title,
                changelog = body,
                releasePageUrl = htmlUrl,
                downloadUrl = apkDownloadUrl,
            )
        }
        null
    }

    private fun fetchLatestRelease(repo: String): JSONObject? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL("https://api.github.com/repos/$repo/releases/latest")
            conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "TG-WS-Proxy-Android")
            }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()
                JSONObject(response)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    fun getCurrentVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "2.3.3"
        } catch (_: Exception) {
            "2.3.3"
        }
    }

    fun isNewerVersion(remoteTag: String, localVersion: String): Boolean {
        val cleanRemote = remoteTag.trim().removePrefix("v")
        val cleanLocal = localVersion.trim().removePrefix("v")

        val remoteParts = cleanRemote.split(".").mapNotNull { it.takeWhile { ch -> ch.isDigit() }.toIntOrNull() }
        val localParts = cleanLocal.split(".").mapNotNull { it.takeWhile { ch -> ch.isDigit() }.toIntOrNull() }

        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }

    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) { }
    }
}
