package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object NewsDestination : NavDestination() {
    override val key = "news"
    override val title = res(Strings.navigation_label_news)
    override val icon = Drawables.stream

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("news")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}