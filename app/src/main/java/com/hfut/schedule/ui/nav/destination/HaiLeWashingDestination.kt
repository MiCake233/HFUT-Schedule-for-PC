package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeWashingScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object HaiLeWashingDestination : NavDestination() {
    override val key = "washing"
    override val title = res(Strings.navigation_label_washing)
    override val icon = Drawables.local_laundry_service

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        HaiLeWashingScreen(vm)
    }
}