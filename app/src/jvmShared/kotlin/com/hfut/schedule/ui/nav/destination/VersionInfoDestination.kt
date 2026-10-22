package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object VersionInfoDestination : NavDestination() {
    override val key = "version_info"
    override val title = res(Strings.navigation_label_version_info)
    override val icon = Drawables.info

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("version_info")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}