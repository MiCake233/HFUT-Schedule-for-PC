package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object WorkDestination : NavDestination() {
    override val key = "work"
    override val title = res(Strings.navigation_label_work)
    override val icon = Drawables.azm

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("work")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}