package com.hfut.schedule.desktop

import java.io.File
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * 应用内更新检查：读取 GitHub Releases 的「最新正式版」，与当前桌面版版本号比较。
 *
 * 安全约定：
 * - 只用 `/releases/latest`，预发布（prerelease）与草稿不会被推给普通用户；
 * - 下载完成后必须与 GitHub 资产列表里的 `digest`（sha256）比对，比对失败直接删除文件、拒绝运行；
 * - 安装包落在数据目录的 `update/` 下（不是 %TEMP%），文件名取自服务端资产名，避免路径注入。
 */
object DesktopUpdate {

    private const val REPO = "MiCake233/HFUT-Schedule-for-PC"
    private const val API_LATEST = "https://api.github.com/repos/$REPO/releases/latest"
    private const val RELEASES_PAGE = "https://github.com/$REPO/releases/latest"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    sealed interface Result {
        data class UpToDate(val current: String) : Result

        data class Available(
            val current: String,
            val latest: String,
            val url: String,
            /** 安装包直链；发布页里没有 .msi 资产时为 null */
            val assetUrl: String? = null,
            val assetName: String? = null,
            /** GitHub 给出的 sha256（小写十六进制，无前缀）；缺失时为 null */
            val sha256: String? = null,
            val assetSize: Long = 0L,
        ) : Result

        data class Failed(val message: String) : Result
    }

    /** 下载结果：成功拿到已校验的文件，或一句可展示给用户的失败原因 */
    sealed interface Download {
        data class Ok(val file: File) : Download
        data class Error(val message: String) : Download
    }

    /** 安装版由 jpackage 注入；开发环境返回 null（自检可用 -Dhfut.current-version 覆盖） */
    fun currentVersion(): String? =
        System.getProperty("hfut.current-version")?.takeIf { it.isNotBlank() }
            ?: runCatching { System.getProperty("jpackage.app-version") }.getOrNull()?.takeIf { it.isNotBlank() }

    fun check(): Result {
        val current = currentVersion() ?: return Result.Failed("当前为开发环境，无版本号")
        val request = Request.Builder()
            .url(API_LATEST)
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "HFUT-Schedule-Desktop")
            .build()
        return runCatching {
            client.newCall(request).execute().use { response ->
                if (response.code == 404) {
                    return@use Result.Failed("上游尚未发布正式版本")
                }
                if (!response.isSuccessful) {
                    return@use Result.Failed("请求失败：HTTP ${response.code}")
                }
                val body = response.body?.string().orEmpty()
                val tag = firstGroup(body, "\"tag_name\"\\s*:\\s*\"([^\"]+)\"")
                    ?: return@use Result.Failed("响应解析失败（缺少 tag_name）")
                val pageUrl = firstGroup(body, "\"html_url\"\\s*:\\s*\"([^\"]+)\"") ?: RELEASES_PAGE
                val latest = tag.removePrefix("v").removePrefix("V")
                if (!isNewer(latest, current)) return@use Result.UpToDate(current)

                val asset = pickInstaller(body)
                Result.Available(
                    current = current,
                    latest = latest,
                    url = pageUrl,
                    assetUrl = asset?.url,
                    assetName = asset?.name,
                    sha256 = asset?.sha256,
                    assetSize = asset?.size ?: 0L,
                )
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

    // ---- 极简 JSON 读取：只取本接口固定的几个字段，避免为一次联网引入序列化依赖 ----

    private data class Asset(
        val name: String,
        val url: String,
        val sha256: String?,
        val size: Long,
    )

    private fun firstGroup(text: String, pattern: String): String? =
        Regex(pattern).find(text)?.groupValues?.getOrNull(1)

    /** 在 `"assets": [ ... ]` 里找出 `.msi` 资产（取第一个），顺带取 digest 与 size */
    private fun pickInstaller(body: String): Asset? {
        val assetsStart = body.indexOf("\"assets\"")
        if (assetsStart < 0) return null
        val arrayStart = body.indexOf('[', assetsStart)
        if (arrayStart < 0) return null
        for (chunk in topLevelObjects(body, arrayStart)) {
            val name = firstGroup(chunk, "\"name\"\\s*:\\s*\"([^\"]+)\"") ?: continue
            if (!name.endsWith(".msi", ignoreCase = true)) continue
            val url = firstGroup(chunk, "\"browser_download_url\"\\s*:\\s*\"([^\"]+)\"") ?: continue
            val digest = firstGroup(chunk, "\"digest\"\\s*:\\s*\"([^\"]+)\"")
            val size = firstGroup(chunk, "\"size\"\\s*:\\s*(\\d+)")?.toLongOrNull() ?: 0L
            return Asset(name, url, digest?.substringAfter(':')?.lowercase(), size)
        }
        return null
    }

    /** 从 `[` 开始按大括号配对切出数组里的每个对象；不做完整 JSON 解析，只求对这个固定结构够用 */
    private fun topLevelObjects(body: String, arrayStart: Int): List<String> {
        val result = mutableListOf<String>()
        var i = arrayStart + 1
        var depth = 0
        var objStart = -1
        var inString = false
        var escaped = false
        while (i < body.length) {
            val c = body[i]
            if (inString) {
                when {
                    escaped -> escaped = false
                    c == '\\' -> escaped = true
                    c == '"' -> inString = false
                }
            } else {
                when (c) {
                    '"' -> inString = true
                    '{' -> if (depth++ == 0) objStart = i
                    '}' -> if (--depth == 0 && objStart >= 0) {
                        result.add(body.substring(objStart, i + 1))
                        objStart = -1
                    }
                    ']' -> if (depth == 0) return result
                }
            }
            i++
        }
        return result
    }

    /**
     * 下载安装包并校验。
     *
     * 校验失败一律删除文件并返回 [Download.Error]——绝不允许把来路不明的可执行文件交给系统运行。
     */
    suspend fun download(
        target: Result.Available,
        onProgress: (Long, Long) -> Unit = { _, _ -> },
    ): Download = withContext(Dispatchers.IO) {
        val url = target.assetUrl
            ?: return@withContext Download.Error("该版本未提供 .msi 安装包，请到发布页手动下载")
        val fileName = target.assetName?.takeIf { it.endsWith(".msi", ignoreCase = true) }
            ?: "HFUT-Schedule-${target.latest}.msi"
        // 只允许纯文件名，杜绝 ../ 之类的路径穿越
        if (fileName.contains('/') || fileName.contains('\\') || fileName.contains("..")) {
            return@withContext Download.Error("安装包文件名异常，已阻止下载")
        }
        val dir = File(com.xah.common.platform.Platform.dataDirPath(), "update").apply { mkdirs() }
        val outFile = File(dir, fileName)
        runCatching { if (outFile.exists()) outFile.delete() }

        val digest = MessageDigest.getInstance("SHA-256")
        val outcome = runCatching {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "HFUT-Schedule-Desktop")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@runCatching Download.Error("下载失败：HTTP ${response.code}")
                val body = response.body ?: return@runCatching Download.Error("下载失败：响应为空")
                val total = body.contentLength()
                body.byteStream().use { input ->
                    outFile.outputStream().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var written = 0L
                        while (true) {
                            val read = input.read(buffer)
                            if (read <= 0) break
                            output.write(buffer, 0, read)
                            digest.update(buffer, 0, read)
                            written += read
                            onProgress(written, total)
                        }
                    }
                }
            }
            verify(outFile, digest.digest(), target.sha256, target.assetSize)
        }.getOrElse { Download.Error(it.message ?: "下载异常") }

        if (outcome is Download.Error) runCatching { outFile.delete() }
        outcome
    }

    /** 供自检使用：按给定的期望值校验一个本地文件 */
    internal fun verifyLocalFile(
        file: File,
        expectedSha256: String?,
        expectedSize: Long = 0L,
    ): Download = verify(file, MessageDigest.getInstance("SHA-256").digest(file.readBytes()), expectedSha256, expectedSize)

    /** 先比 sha256（服务端给了就必须一致），再用文件大小与 OLE 头兜底 */
    private fun verify(file: File, actual: ByteArray, expectedSha256: String?, expectedSize: Long): Download {
        if (!file.isFile || file.length() == 0L) return Download.Error("下载得到的文件为空")
        if (expectedSha256 != null) {
            val hex = actual.joinToString("") { "%02x".format(it) }
            if (!hex.equals(expectedSha256, ignoreCase = true)) {
                return Download.Error("安装包校验失败（SHA-256 不匹配），已阻止运行")
            }
        } else if (expectedSize > 0 && file.length() != expectedSize) {
            return Download.Error("安装包大小与发布信息不一致，已阻止运行")
        }
        // MSI 是 OLE 复合文档，头 4 字节固定为 D0 CF 11 E0
        val head = ByteArray(4)
        val read = runCatching { file.inputStream().use { it.read(head) } }.getOrDefault(0)
        val ole = read == 4 &&
            head[0] == 0xD0.toByte() && head[1] == 0xCF.toByte() &&
            head[2] == 0x11.toByte() && head[3] == 0xE0.toByte()
        if (!ole) return Download.Error("下载到的不是有效的安装包（文件头异常），已阻止运行")
        return Download.Ok(file)
    }

    /** 用系统默认方式启动安装包 */
    fun launchInstaller(file: File): Boolean = runCatching {
        if (!file.isFile) return false
        if (!Desktop.isDesktopSupported()) return false
        Desktop.getDesktop().open(file)
        true
    }.getOrDefault(false)
}

/** 启动时检测到的新版本，供窗口层弹窗提示 */
object DesktopUpdateState {
    var available by androidx.compose.runtime.mutableStateOf<DesktopUpdate.Result.Available?>(null)
    var dismissed by androidx.compose.runtime.mutableStateOf(false)

    fun clear() {
        available = null
        dismissed = false
    }
}
