package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object CommunityAppointmentDestination : NavDestination() {
    override val key = "community_appointment"
    override val title = res(Strings.navigation_label_community_appointment)
    override val icon = Drawables.table_restaurant

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("community_appointment")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}