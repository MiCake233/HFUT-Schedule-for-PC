package com.hfut.schedule.logic.platform

/**
 * 平台能力开关：用于把「Android 有、桌面没有」的功能在共享代码里优雅降级，
 * 而不是到处写 if (isAndroid)。
 */
expect object AppCapabilities {
    /** 当前是否运行在 Android 上（桌面端为 false），用于隐藏只在手机上成立的功能入口 */
    fun isAndroidPlatform(): Boolean

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
    /** 关闭当前页面/窗口 */
    fun finishCurrentScreen()

    /** 清理应用缓存，返回释放的 MB 数 */
    suspend fun cleanCache(): Double

    /** 分享文本/链接 */
    fun shareText(text: String)

    /** 挂载桌面快捷方式（桌面端无此概念，空实现） */
    fun initShortcuts(any: Any?, sort: Any?)

    /** 读取系统日历列表（桌面端返回空） */
    fun queryCalendars(): List<Pair<Long, String>>
    /** 屏幕高度（dp）。Android 取配置；桌面端取默认值 */
    fun screenHeightDp(): Int

    /**
     * 把文本写成文件并「分享」出去。
     * Android：写入应用外部目录后调起系统分享面板；桌面：写入下载目录并打开所在文件夹。
     * @return 实际写入的绝对路径（失败返回 null）
     */
    fun saveAndShareTextFile(fileName: String, content: String, chooserTitle: String): String?

    /** 按文本内容打开对应应用或网页（学习通 / U 校园 / 雨课堂 / MOOC / 外链） */
    fun openOperation(info: String)

    /** 退出整个应用 */
    fun exitApplication()

    /**
     * 是否支持渐进式模糊（haze 的 HazeProgressive）。
     * desktop 版 haze 的着色器与当前 Compose 版本的 shader 签名不兼容，
     * 桌面端改用等价的渐变叠加来实现同样的观感。
     */
    fun supportsProgressiveBlur(): Boolean
}