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
}