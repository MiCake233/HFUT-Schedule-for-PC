package com.hfut.schedule.desktop

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.Request

/** 应用内更新检查：读取 GitHub Releases，与当前桌面版版本号比较。 */
object DesktopUpdate {

    private const val REPO = "MiCake233/HFUT-Schedule-for-PC"
    private const val API = "https://api.github.com/repos/$REPO/releases"
    private const val DOWNLOAD_BASE = "https://github.com/$REPO/releases/download"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    sealed interface Result {
        data class UpToDate(val current: String) : Result
        data class Available(val current: String, val latest: String, val url: String) : Result {
            /** 安装包直链：仓库命名规范固定为 HFUT-Schedule-<版本>.msi */
            val assetUrl: String
                get() = "$DOWNLOAD_BASE/v$latest/HFUT-Schedule-$latest.msi"
        }
        data class Failed(val message: String) : Result
    }

    /** 安装版由 jpackage 注入；开发环境返回 null */
    fun currentVersion(): String? =
        runCatching { System.getProperty("jpackage.app-version") }.getOrNull()

    fun check(): Result {
        val current = currentVersion() ?: return Result.Failed("当前为开发环境，无版本号")
        val request = Request.Builder()
            .url(API)
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "HFUT-Schedule-Desktop")
            .build()
        return runCatching {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use Result.Failed("请求失败：HTTP ${response.code}")
                val body = response.body?.string().orEmpty()
                val tag = Regex("\"tag_name\"\\s*:\\s*\"([^\"]+)\"").find(body)?.groupValues?.get(1)
                    ?: return@use Result.Failed("响应解析失败")
                val url = Regex("\"html_url\"\\s*:\\s*\"([^\"]+)\"").find(body)?.groupValues?.get(1)
                    ?: return@use Result.Failed("响应解析失败")
                val latest = tag.removePrefix("v").removePrefix("V")
                if (isNewer(latest, current)) {
                    Result.Available(current, latest, url)
                } else {
                    Result.UpToDate(current)
                }
            }
        }.getOrElse { Result.Failed(it.message ?: "网络异常") }
    }

    /** 语义化版本比较：逐段按数字比较，缺失段按 0 处理。 */
    private fun isNewer(candidate: String, current: String): Boolean {
        val a = candidate.split('.', '-', '+').mapNotNull { it.toIntOrNull() }
        val b = current.split('.', '-', '+').mapNotNull { it.toIntOrNull() }
        if (a.isEmpty() || b.isEmpty()) return false
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }

    /**
     * 下载安装包并启动安装程序。
     * @return 下载到的文件；失败返回 null
     */
    suspend fun download(target: Result.Available, onProgress: (Long, Long) -> Unit = { _, _ -> }): File? =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url(target.assetUrl)
                    .header("User-Agent", "HFUT-Schedule-Desktop")
                    .build()
                val dir = File(System.getProperty("java.io.tmpdir"), "HFUT-Schedule-update").apply { mkdirs() }
                val outFile = File(dir, "HFUT-Schedule-${target.latest}.msi")
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@runCatching null
                    val body = response.body ?: return@runCatching null
                    val total = body.contentLength()
                    body.byteStream().use { input ->
                        outFile.outputStream().use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var written = 0L
                            while (true) {
                                val read = input.read(buffer)
                                if (read <= 0) break
                                output.write(buffer, 0, read)
                                written += read
                                onProgress(written, total)
                            }
                        }
                    }
                }
                outFile
            }.getOrNull()
        }

    /** 用系统默认方式启动安装包 */
    fun launchInstaller(file: File): Boolean = runCatching {
        if (!Desktop.isDesktopSupported()) return false
        Desktop.getDesktop().open(file)
        true
    }.getOrDefault(false)
}
