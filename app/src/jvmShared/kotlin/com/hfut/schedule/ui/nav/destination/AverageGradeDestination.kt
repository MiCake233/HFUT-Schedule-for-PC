package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class AverageGradeDestination(
    val useUniAppData : Boolean
) : NavDestination() {
    override val key = "${KEY}_$useUniAppData"
    override val description = if(useUniAppData) "合工大教务数据源" else "教务系统数据源"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "average_grade"
        val TITLE = res(Strings.navigation_label_average_grade)
        val ICON = Drawables.leaderboard
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("_$useUniAppData")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}