package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.logic.util.sys.CourseLiveUpdateScheduler
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object CourseLiveUpdateDetailDestination : NavDestination() {
    override val key = "course_live_update_detail"
    override val title = res(Strings.navigation_label_course_detail)
    override val icon = Drawables.category

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("course_live_update_detail")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}
