package com.hfut.schedule.logic.platform

actual object AppCapabilities {
    actual fun isOcrModelAvailable(): Boolean = false

    actual fun shortcutStorageStr(): String = ""

    // 桌面端不写系统日历（后续可改为导出 .ics）
    actual fun canWriteCalendar(): Boolean = false

    actual fun canPickFile(): Boolean = true

    actual fun packageName(): String = "com.hfut.schedule"
    actual fun finishCurrentScreen() {
        // 桌面端由导航栈负责返回，这里不做处理
    }

    actual suspend fun cleanCache(): Double = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        var freed = 0L
        runCatching {
            java.io.File(com.xah.common.platform.Platform.cacheDirPath()).listFiles()?.forEach { dir ->
                freed += dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
                dir.deleteRecursively()
            }
        }
        freed / 1024.0 / 1024.0
    }

    actual fun shareText(text: String) {
        runCatching { java.awt.Toolkit.getDefaultToolkit().systemClipboard.setContents(java.awt.datatransfer.StringSelection(text), null) }
    }

    actual fun initShortcuts(any: Any?, sort: Any?) = Unit

    actual fun queryCalendars(): List<Pair<Long, String>> = emptyList()
    actual fun screenHeightDp(): Int = 900

    actual fun saveAndShareTextFile(fileName: String, content: String, chooserTitle: String): String? = runCatching {
        // 桌面端：写到「下载」目录，然后用资源管理器定位到该文件
        val downloads = java.io.File(System.getProperty("user.home"), "Downloads")
        val dir = if (downloads.isDirectory) downloads else java.io.File(System.getProperty("user.home"))
        dir.mkdirs()
        val file = java.io.File(dir, fileName)
        file.writeText(content)
        java.awt.Desktop.getDesktop().open(dir)
        com.hfut.schedule.logic.util.sys.showToast("已导出到 ${file.absolutePath}")
        file.absolutePath
    }.getOrNull()

    actual fun openOperation(info: String) {
        when {
            info.contains("U校园") -> com.hfut.schedule.logic.util.sys.Starter.startWebUrlOuter(
                if (info.contains("AI", ignoreCase = true))
                    com.hfut.schedule.network.api.model.Constant.UNIPUS_AI_URL
                else com.hfut.schedule.network.api.model.Constant.UNIPUS_URL
            )
            com.hfut.schedule.logic.util.sys.isValidWebUrl(info) ->
                com.hfut.schedule.logic.util.sys.Starter.startWebUrlOuter(info)
            else -> com.hfut.schedule.logic.util.sys.showToast("此条未做点击适配")
        }
    }

    actual fun exitApplication() {
        val hook = com.hfut.schedule.logic.util.sys.PlatformHooks.onExitApp
        if (hook != null) hook() else kotlin.system.exitProcess(0)
    }

    // haze 桌面版（1.6.2）的 skia 着色器与 Compose 1.11 的 ShaderBrush 签名不一致，
    // 渐进模糊会抛 NoSuchMethodError，这里关掉它，改由渐变叠加实现同样的观感。
    actual fun supportsProgressiveBlur(): Boolean = false
}