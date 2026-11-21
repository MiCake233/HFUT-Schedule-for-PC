package com.hfut.schedule.logic.platform

/**
 * 功能可用性判定。
 *
 * 仅存在于 Android 的页面（相机扫码、内嵌 WebView、磁贴、官方 App 深链等）在桌面端不显示入口。
 * 走普通网络接口或系统浏览器的功能不在此列。
 */
object PlatformFeatures {

    /** 桌面端留给仍展示的 Android 专属操作的标注。 */
    const val DESKTOP_UNSUPPORTED = "暂不支持"

    /**
     * 依赖 Android 系统能力的 Destination key。
     * 前缀匹配：`scan_qr_code` 同时覆盖带后缀的扫码页。
     */
    private val ANDROID_ONLY_KEYS = listOf(
        "scan_qr_code",          // 相机扫码
        "office_hall",           // 办事大厅（Android WebView，页面不适配系统浏览器）
        "control_center",        // 快捷控制中心（磁贴）
        "update_history",        // 更新历史（Android 更新模块）
        "xwx_home",              // 小物星主页（Android 专属实现）
        "express",               // 拼多多 / 淘宝身份码，只能拉起 Android 应用
    )

    private val ANDROID_ONLY_FUNC_IDS = setOf(
        2,   // 扫码（相机）
        23,  // 热水（支付宝小程序深链）
        43,  // 办事大厅
        49,  // 快递（拼多多 / 淘宝）
    )

    private fun isAndroidOnly(key: String): Boolean =
        ANDROID_ONLY_KEYS.any { key == it || key.startsWith(it) }

    /** 该 Destination 在当前平台是否可用；Android 端恒为 true。 */
    fun supports(key: String?): Boolean {
        if (key.isNullOrEmpty()) return true
        if (AppCapabilities.isAndroidPlatform()) return true
        return !isAndroidOnly(key)
    }

    /** 聚焦页 / 搜索页的功能条目（GlobalUiStateHolder.funcDefault）是否可用。 */
    fun supportsFunc(id: Int): Boolean =
        AppCapabilities.isAndroidPlatform() || id !in ANDROID_ONLY_FUNC_IDS

    /**
     * 拉起官方 Android 应用或 `alipays://` 之类的深链。
     * 桌面端没有这些程序，调用只会失败。
     */
    fun supportsAndroidAppLink(): Boolean = AppCapabilities.isAndroidPlatform()
}
