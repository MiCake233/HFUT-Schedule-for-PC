package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.screen.home.SearchEditScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object FunctionsSortDestination : NavDestination() {
    override val key = "functions_sort"
    override val title = res(Strings.navigation_label_functions_sort)
    override val icon = Drawables.edit

    @Composable
    override fun Content() {
        SearchEditScreen()
    }
}