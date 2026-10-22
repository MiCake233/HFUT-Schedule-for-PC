package com.hfut.schedule.ui.screen.home.search.function.school.webvpn

/**
 * 兼容旧包路径：getWebVpnCookie 已上移到 logic.util.sys（跨平台）。
 */
suspend fun getWebVpnCookie(): String? = com.hfut.schedule.logic.util.sys.getWebVpnCookie()
