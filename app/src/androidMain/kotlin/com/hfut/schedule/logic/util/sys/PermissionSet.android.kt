package com.hfut.schedule.logic.util.sys

import android.app.Activity

actual object PermissionSet {
    actual fun checkAndRequestStoragePermission(any: Any?) {
        (any as? Activity)?.let { checkAndRequestStoragePermission(it) }
    }

    actual fun checkAndRequestCalendarPermission(any: Any?) {
        (any as? Activity)?.let { checkAndRequestCalendarPermission(it) }
    }

    actual fun checkAndRequestCameraPermission(any: Any?) {
        (any as? Activity)?.let { checkAndRequestCameraPermission(it) }
    }

    actual fun checkAndRequestNotificationPermission(any: Any?) {
        (any as? Activity)?.let { checkAndRequestNotificationPermission(it) }
    }
}

@androidx.compose.runtime.Composable
actual fun currentPlatformActivity(): Any? = androidx.activity.compose.LocalActivity.current

actual fun currentPlatformActivityOrNull(): Any? =
    runCatching { com.hfut.schedule.application.MyApplication.getCurrentActivity() }.getOrNull()
