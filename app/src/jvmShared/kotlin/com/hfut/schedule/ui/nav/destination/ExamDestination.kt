package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class ExamDestination(
    val origin : String?,
) : NavDestination() {
    override val key = "${KEY}_$origin"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "exam"
        val TITLE = res(Strings.navigation_label_exam)
        val ICON = Drawables.draw
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("_$origin")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}