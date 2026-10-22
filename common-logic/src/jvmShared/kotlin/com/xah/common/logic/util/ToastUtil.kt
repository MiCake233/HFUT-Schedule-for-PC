package com.xah.common.logic.util

import com.xah.common.platform.Platform

/**
 * 轻量提示。Android: Toast；桌面: 由 UI 层注册的浮层提示。
 * 跨平台后不再需要手动传 Context。
 */
object ToastUtil {
    fun showToast(msg: String) = Platform.toast(msg)
}
