package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class WorkAndRestDestination(
    val name : String?
) : NavDestination() {
    override val key = "${KEY}_$name"
    override val title = TITLE
    override val description = name
    override val icon = ICON

    companion object {
        const val KEY = "work_and_rest"
        val TITLE = res(Strings.navigation_label_work_and_rest)
        val ICON = Drawables.schedule
    }

    @Composable
    override fun Content() {
        TimeTableScreen(name)
    }
}