package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class ClassroomDestination(
    val origin : String = "Search"
) : NavDestination() {
    override val key = "${KEY}_$origin"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "classroom"
        val TITLE = res(Strings.navigation_label_classroom)
        val ICON = Drawables.meeting_room
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("classroom#$origin")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}