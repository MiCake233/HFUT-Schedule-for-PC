package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class TermCoursesDestination(
    val ifSaved : Boolean,
    val origin : String,
) : NavDestination() {
    override val key = "${KEY}_${ifSaved}_$origin"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "all_course"
        val TITLE = res(Strings.navigation_label_term_courses)
        val ICON = Drawables.category
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformDestination(this)) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}