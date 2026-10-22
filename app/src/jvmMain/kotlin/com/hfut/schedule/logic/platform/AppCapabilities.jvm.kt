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
        kotlin.system.exitProcess(0)
    }
}