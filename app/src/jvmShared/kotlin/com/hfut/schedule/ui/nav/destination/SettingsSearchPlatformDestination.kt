package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.RenderPlatformScreen
import com.hfut.schedule.ui.nav.UnavailableFeature
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.util.text

/** 设置项搜索页：真正的实现在 Android 端（SettingsSearchDestination） */
object SettingsSearchPlatformDestination : NavDestination() {
    override val key: String = "platform_settings_search"
    override val title: UiText = text("搜索设置")
    override val icon: Int = Drawables.search

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}
