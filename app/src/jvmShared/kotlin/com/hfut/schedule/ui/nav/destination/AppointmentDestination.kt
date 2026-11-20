package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.Strings

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object AppointmentDestination : NavDestination() {
    override val key = "appointment"
    override val title = res(Strings.navigation_label_community_appointment)
    override val icon = Drawables.where_to_vote

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformDestination(this)) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}
