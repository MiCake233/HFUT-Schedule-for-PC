package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.RenderPlatformScreen
import com.hfut.schedule.ui.nav.UnavailableFeature
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.Strings
import com.xah.common.ui.util.res
import com.xah.common.ui.util.text

/**
 * 「Android 端才实现的设置页」的共享壳。
 *
 * 共享层页面（实验室、版本信息等）需要跳到这些设置页，但真正的界面仍留在 androidMain。
 * 这里统一用 [RenderPlatformScreen] 渲染：Android 端由平台注册表分发到真实实现，
 * 桌面端显示 [UnavailableFeature] 占位。key 与 Android 端实际目的地刻意区分开，
 * 避免同一个 key 在导航栈里被注册两次。
 */
object SettingsAppearancePlatformDestination : NavDestination() {
    override val key: String = "platform_settings_appearance"
    override val title: UiText = res(Strings.navigation_label_settings_appearance)
    override val icon: Int = Drawables.format_paint

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}

/** 教务系统密码设置页的共享壳，见 [SettingsAppearancePlatformDestination] */
object SettingsJxglstuPasswordPlatformDestination : NavDestination() {
    override val key: String = "platform_settings_jxglstu_password"
    override val title: UiText = text("教务系统密码")
    override val icon: Int = Drawables.lock

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}
