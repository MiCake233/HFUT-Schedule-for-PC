package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object OfficeHallDestination : NavDestination() {
    override val key = "office_hall"
    override val title = res(Strings.navigation_label_office_hall)
    override val icon = Drawables.person_play

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("office_hall")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}