package com.hfut.schedule.logic.platform

import com.hfut.schedule.logic.util.ocr.TesseractUtils
import com.hfut.schedule.logic.util.shortcut.AppShortcutManager

actual object AppCapabilities {
    actual fun isOcrModelAvailable(): Boolean = runCatching { TesseractUtils.isExistModule() }.getOrDefault(false)

    actual fun shortcutStorageStr(): String = runCatching { AppShortcutManager.getStorageStr() }.getOrDefault("")

    actual fun canWriteCalendar(): Boolean = true

    actual fun canPickFile(): Boolean = true

    actual fun packageName(): String = com.hfut.schedule.application.MyApplication.context.packageName
    actual fun finishCurrentScreen() {
        com.hfut.schedule.application.MyApplication.getCurrentActivity()?.finish()
    }

    actual suspend fun cleanCache(): Double = runCatching {
        com.hfut.schedule.logic.util.storage.file.cleanCache(
            com.hfut.schedule.application.MyApplication.context
        )
    }.getOrDefault(0.0)

    actual fun shareText(text: String) {
        runCatching {
            val ctx = com.hfut.schedule.application.MyApplication.context
            ctx.startActivity(
                android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, text)
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    actual fun initShortcuts(any: Any?, sort: Any?) {
        runCatching { com.hfut.schedule.logic.util.shortcut.AppShortcutManager.init(any as android.content.Context, sort as? String) }
    }

    actual fun queryCalendars(): List<Pair<Long, String>> =
        runCatching { com.hfut.schedule.logic.util.sys.queryCalendars() }.getOrDefault(emptyList())
    actual fun screenHeightDp(): Int =
        com.hfut.schedule.application.MyApplication.context.resources.configuration.screenHeightDp

    actual fun saveAndShareTextFile(fileName: String, content: String, chooserTitle: String): String? = runCatching {
        val ctx = com.hfut.schedule.application.MyApplication.context
        val file = java.io.File(ctx.getExternalFilesDir(null), fileName)
        file.writeText(content)
        val uri = androidx.core.content.FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.provider",
            file
        )
        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(
            android.content.Intent.createChooser(shareIntent, chooserTitle)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        file.absolutePath
    }.getOrNull()

    actual fun openOperation(info: String) {
        com.hfut.schedule.ui.screen.home.focus.funiction.openOperation(
            info,
            com.hfut.schedule.application.MyApplication.context
        )
    }

    actual fun exitApplication() {
        runCatching { com.hfut.schedule.logic.util.storage.file.killAppUnSafely() }
    }

    actual fun supportsProgressiveBlur(): Boolean = true
}