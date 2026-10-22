package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.util.sys.activityOrNull

/**
 * 权限申请。
 * Android 需要真的申请存储/日历/相机/通知权限；桌面端没有这些概念，
 * 全部降级为空实现，调用方无需任何改动。
 */
expect object PermissionSet {
    fun checkAndRequestStoragePermission(any: Any?)
    fun checkAndRequestCalendarPermission(any: Any?)
    fun checkAndRequestCameraPermission(any: Any?)
    fun checkAndRequestNotificationPermission(any: Any?)
}
/** 当前页面对应的平台窗口（Android: Activity；桌面: null） */
@androidx.compose.runtime.Composable
expect fun currentPlatformActivity(): Any?
/**
 * 兼容旧代码里的 `activity?.xxx` 写法。
 * Android 返回当前 Activity，桌面端返回 null（调用链自然跳过）。
 */
@androidx.compose.runtime.Composable
fun activityOrNull(): Any? = currentPlatformActivity()