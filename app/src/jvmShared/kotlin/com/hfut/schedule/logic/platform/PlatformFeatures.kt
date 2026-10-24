package com.hfut.schedule.logic.platform

/**
 * 功能可用性判定。
 *
 * 仅存在于 Android 的页面（相机扫码、内嵌 WebView、磁贴、PDF 导出等）在桌面端不显示入口。
 */
object PlatformFeatures {

    /**
     * 依赖 Android 系统能力的 Destination key。
     * 前缀匹配：`select_courses` 同时覆盖 `select_courses_detail_*`。
     */
    private val ANDROID_ONLY_KEYS = listOf(
        "scan_qr_code",          // 相机扫码
        "office_hall",           // 办事大厅（WebView）
        "control_center",        // 快捷控制中心（磁贴）
        "guagua_login",          // 淋浴登录（WebView）
        "update_history",        // 更新历史（Android 更新模块）
        "xwx_home",              // 小物星主页（Android 专属实现）
    )

    private val ANDROID_ONLY_FUNC_IDS = setOf(
        2,   // 扫码
        43,  // 办事大厅
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
}
