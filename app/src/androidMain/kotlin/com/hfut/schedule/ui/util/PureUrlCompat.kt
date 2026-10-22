package com.hfut.schedule.ui.util

/**
 * 兼容旧包路径：getPureUrl 已上移到 logic.util.sys（跨平台），
 * Android 侧仍按老包名引用，这里做一层转发。
 */
fun getPureUrl(url: String): String = com.hfut.schedule.logic.util.sys.getPureUrl(url)
