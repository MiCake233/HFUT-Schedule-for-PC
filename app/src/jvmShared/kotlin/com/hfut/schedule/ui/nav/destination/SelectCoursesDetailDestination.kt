package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class SelectCoursesDetailDestination(
    val index : Int,
    val name : String
) : NavDestination() {
    override val key = "select_courses_detail_${name}_$index"
    override val description = name
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(Strings.navigation_label_select_courses_detail)
        val ICON = Drawables.ads_click
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("select_courses_detail#$index")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}