package com.hfut.schedule.logic.model

import com.hfut.schedule.network.api.model.Constant
import com.xah.common.logic.model.Campus

/**
 * 全应用共用常量。
 * 原先这些散落在 Android 的 `MyApplication` 伴生对象里，桌面端无法引用，
 * 抽成独立的跨平台对象；`MyApplication` 仍保留同名成员以兼容 Android 侧旧代码。
 */
object AppConst {
    // 方格默认高度
    const val CALENDAR_SQUARE_HEIGHT = 70f
    const val CALENDAR_SQUARE_TEXT_PADDING = 1.35f
    const val CALENDAR_SQUARE_ALPHA = 0.6f
    const val CALENDAR_SQUARE_BLUR = 0.2f
    const val SWIPE = 5f
    // HAZE模糊半径
    const val BLUR_RADIUS = 20
    // 宣城校区免费流量额度 GiB
    const val DEFAULT_MAX_FREE_FLOW = 200
    // 最大周
    const val MAX_WEEK = 20
    // App名称
    const val APP_NAME = "聚在工大"
    // 邮箱后缀
    const val EMAIL = "@mail.hfut.edu.cn"

    // 三个校区的经纬度 来自高德地图坐标拾取器
    val campusLocations by lazy {
        mapOf(
            Campus.FCH to Location(117.20346, 31.77014),
            Campus.TXL to Location(117.29597, 31.843905),
            Campus.XC to Location(118.710182, 30.903593)
        )
    }

    // 贡献者
    val contributors by lazy {
        // 通过 https://api.github.com/users/${用户名}获取ID
        mapOf(
            Constant.GITHUB_DEVELOPER_NAME to 116127902,
            "tinyvan" to 27542299,
            "linsui" to 36977733,
            "James-Zhang2" to 175417444,
            "Today1337" to 110648923,
            "zxbmmmmmmmmm" to 96322503,
            "Junpgle" to 81464408,
        )
    }
}
