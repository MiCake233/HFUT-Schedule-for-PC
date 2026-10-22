package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object TermReportDestination : NavDestination() {
    override val key = "term_report"
    override val title = res(Strings.navigation_label_term_report)
    override val icon = Drawables.kid_star

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("term_report")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}
