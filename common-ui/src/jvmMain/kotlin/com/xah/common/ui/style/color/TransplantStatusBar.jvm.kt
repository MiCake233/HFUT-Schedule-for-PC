package com.xah.common.ui.style.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 桌面端没有系统状态栏/导航栏，保持与 Android 相同的公开 API 但什么都不做。
 * 将来若要联动窗口标题栏配色，可在此实现。
 */
@Composable
fun TransparentSystemBars(isBackgroundLight: Boolean) {
    // no-op on desktop
}

@Composable
fun TransparentSystemBars2(backgroundColor: Color? = null) {
    // no-op on desktop
}

@Composable
fun TransparentSystemBars(backgroundColor: Color? = null) {
    // no-op on desktop
}
