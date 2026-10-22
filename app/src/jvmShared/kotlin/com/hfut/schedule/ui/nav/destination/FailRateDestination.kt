package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object FailRateDestination : NavDestination() {
    override val key = "fail_rate"
    override val title = res(Strings.navigation_label_fail_rate)
    override val icon = Drawables.radio_button_partial

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("fail_rate")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}