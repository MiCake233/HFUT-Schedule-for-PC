package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramSearchScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class AllProgramsDestination(
    val ifSaved : Boolean
) : NavDestination() {
    override val key = "${KEY}_$ifSaved"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "all_program"
        val TITLE = res(Strings.navigation_label_all_programs)
        val ICON = Drawables.conversion_path
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramSearchScreen(vm,ifSaved)
    }
}