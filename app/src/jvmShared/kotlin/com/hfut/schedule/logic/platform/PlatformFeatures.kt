package com.hfut.schedule.logic.platform

/**
 * 功能可用性：把「只在 Android 上成立」的功能入口在共享 UI 里统一隐藏。
 *
 * 桌面版（Windows）没有相机扫码、内嵌 WebView、控制中心磁贴、SAF 文件选择等系统能力，
 * 对应页面在 `androidMain` 里实现，桌面端只能显示「该功能依赖 Android 系统能力」的占位页。
 * 与其让用户点进去看到占位，不如**直接把入口去掉** —— 这里就是唯一的判定源：
 * 共享的列表/卡片在渲染入口前问一句 [PlatformFeatures.supports] / [supportsFunc]。
 *
 * 注意：桌面端已经**实际移植**的功能（成绩、课表、一卡通、图书馆、洗浴、电费、
 * 失物招领、通知、Supabase……）不在此列，见 `PlatformScreens.jvm.kt`。
 */
object PlatformFeatures {

    /**
     * 依赖 Android 系统能力的 Destination key。
     * 前缀匹配：`select_courses` 同时覆盖 `select_courses_detail_*`。
     */
    private val ANDROID_ONLY_KEYS = listOf(
        "scan_qr_code",          // 相机扫码
        "webvpn",                // 内嵌 WebView 登录
        "office_hall",           // 办事大厅（WebView）
        "control_center",        // 快捷控制中心（磁贴）
        "term_report",           // 学期报告（Android PDF 导出）
        "guagua_login",          // 淋浴登录（WebView）
        "person_info",           // 个人信息（Android 专属布局）
        "select_courses",        // 选课（含详情页）
        "drop_courses_",         // 退课
        "transfer_major",        // 转专业（含详情页）
        "update_history",        // 更新历史（Android 更新模块）
        "calendar_config",       // 日历/小组件配置
        "xwx_home",              // 小物星主页（Android 专属实现）
    )

    private val ANDROID_ONLY_FUNC_IDS = setOf(
        2,   // 扫码
        10,  // 个人信息
        13,  // 选课
        29,  // 转专业
        42,  // WebVPN
        43,  // 办事大厅
    )

    private fun isAndroidOnly(key: String): Boolean =
        ANDROID_ONLY_KEYS.any { key == it || key.startsWith(it) }

    /**
     * 该 Destination（按 key）在当前平台是否可用。
     * Android 端恒为 true；桌面端对 [ANDROID_ONLY_KEYS] 返回 false。
     */
    fun supports(key: String?): Boolean {
        if (key.isNullOrEmpty()) return true
        if (AppCapabilities.isAndroidPlatform()) return true
        return !isAndroidOnly(key)
    }

    /**
     * 聚焦页 / 搜索页的功能条目（见 GlobalUiStateHolder.funcDefault）是否可用。
     * id 与功能一一对应，Android 端不做过滤。
     */
    fun supportsFunc(id: Int): Boolean =
        AppCapabilities.isAndroidPlatform() || id !in ANDROID_ONLY_FUNC_IDS
}
