package com.hfut.schedule.ui.nav

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination

/** 桌面端没有平台专属页面 */
@Composable
actual fun RenderPlatformScreen(key: String): Boolean = false

/** 桌面端同理：所有 Android 专属页面都返回 false，由调用方显示占位 */
@Composable
actual fun RenderPlatformDestination(destination: NavDestination): Boolean = false
