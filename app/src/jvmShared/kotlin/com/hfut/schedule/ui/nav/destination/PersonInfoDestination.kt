package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object PersonInfoDestination : NavDestination() {
    override val key = "person_info"
    override val title = res(Strings.navigation_label_person_info)
    override val icon = Drawables.person

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("person_info")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}