package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.ClassmatesScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object ClassmatesDestination : NavDestination() {
    override val key = "classmates"
    override val title = res(Strings.navigation_label_classmates)
    override val icon = Drawables.sensor_door

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassmatesScreen(vm)
    }
}