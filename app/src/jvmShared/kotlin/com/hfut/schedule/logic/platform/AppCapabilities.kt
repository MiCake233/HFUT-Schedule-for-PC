package com.hfut.schedule.logic.platform

/**
 * 平台能力开关：用于把「Android 有、桌面没有」的功能在共享代码里优雅降级，
 * 而不是到处写 if (isAndroid)。
 */
expect object AppCapabilities {
    /** 验证码识别模型是否可用（桌面端暂不支持 OCR） */
    fun isOcrModelAvailable(): Boolean

    /** Android 桌面快捷方式的持久化串；桌面端没有快捷方式，返回空串 */
    fun shortcutStorageStr(): String

    /** 是否支持系统日历写入 */
    fun canWriteCalendar(): Boolean

    /** 是否支持相册/文件选择等系统交互 */
    fun canPickFile(): Boolean

    /** 应用包名/标识：Android 取 Context.packageName，桌面端为固定值 */
    fun packageName(): String
}
