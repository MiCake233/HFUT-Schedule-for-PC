package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object SchoolCardDestination : NavDestination() {
    override val key = "school_card"
    override val title = text("校园一卡通")
    override val icon = Drawables.credit_card

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("school_card")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}