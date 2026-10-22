package com.hfut.schedule.logic.platform

actual object AppCapabilities {
    actual fun isOcrModelAvailable(): Boolean = false

    actual fun shortcutStorageStr(): String = ""

    // 桌面端不写系统日历（后续可改为导出 .ics）
    actual fun canWriteCalendar(): Boolean = false

    actual fun canPickFile(): Boolean = true

    actual fun packageName(): String = "com.hfut.schedule"
}
