package com.hfut.schedule.logic.util.sys

import com.xah.common.logic.util.ToastUtil
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Strings

// 跨平台：不再需要 Context（Android 走 Toast，桌面走浮层提示，由 Platform 分发）
fun showDevelopingToast() = showToast(Strings.toast_developing)

fun showToast(text: String) = ToastUtil.showToast(text)

fun showToast(text: UiText) = ToastUtil.showToast(text.asStringNow()!!)

fun showToast(@Suppress("UNUSED_PARAMETER") resId: Int) = ToastUtil.showToast(com.xah.common.ui.res.stringOf(resId))