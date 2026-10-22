package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class SurveyDestination(
    val ifSaved : Boolean
) : NavDestination() {
    override val key = "${KEY}_$ifSaved"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "survey"
        val TITLE = res(Strings.navigation_label_survey)
        val ICON = Drawables.verified
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("_$ifSaved")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}