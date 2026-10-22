package com.hfut.schedule.ui.nav

import androidx.compose.runtime.Composable
import com.hfut.schedule.logic.util.sys.PlatformHooks
import com.hfut.schedule.ui.nav.destination.base.NavDestination

/**
 * 桌面端没有 Android 那套平台页面，
 * 但如果窗口层为某个 key 注册了实现（PlatformHooks.platformScreenFactory），就渲染它。
 */
@Composable
actual fun RenderPlatformScreen(key: String): Boolean {
    val factory = PlatformHooks.platformScreenFactory ?: return false
    val content = factory(key) ?: return false
    content()
    return true
}

/**
 * 桌面端：Destination 级别同理。
 * 共享层的 Destination 若在桌面有专门实现，可由工厂按 key 提供。
 */
@Composable
actual fun RenderPlatformDestination(destination: NavDestination): Boolean {
    val factory = PlatformHooks.platformScreenFactory ?: return false
    val content = factory(destination.key) ?: return false
    content()
    return true
}
