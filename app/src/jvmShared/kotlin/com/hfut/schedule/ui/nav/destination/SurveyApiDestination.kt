package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

class SurveyApiDestination(
    val lessonCode : String,
) : NavDestination() {
    override val key = "survey_$lessonCode"
    override val description = lessonCode
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(Strings.navigation_label_survey)
        val ICON = Drawables.verified
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("survey_$lessonCode")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}