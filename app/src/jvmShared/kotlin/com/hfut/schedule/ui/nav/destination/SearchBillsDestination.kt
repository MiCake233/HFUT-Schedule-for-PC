package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.card.function.SearchBillsUI
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object SearchBillsDestination : NavDestination() {
    override val key = "search_bills"
    override val title = text("账单搜索")
    override val icon = Drawables.search

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SearchBillsUI(vm)
    }
}