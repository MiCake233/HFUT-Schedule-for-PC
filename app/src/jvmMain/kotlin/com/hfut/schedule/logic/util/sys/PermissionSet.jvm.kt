package com.hfut.schedule.logic.util.sys

actual object PermissionSet {
    actual fun checkAndRequestStoragePermission(any: Any?) = Unit
    actual fun checkAndRequestCalendarPermission(any: Any?) = Unit
    actual fun checkAndRequestCameraPermission(any: Any?) = Unit
    actual fun checkAndRequestNotificationPermission(any: Any?) = Unit
}

/** 桌面端没有 Activity 概念，恒为 null；调用方 `activity?.let {}` 会自然跳过 */
@androidx.compose.runtime.Composable
actual fun currentPlatformActivity(): Any? = null

actual fun currentPlatformActivityOrNull(): Any? = null
