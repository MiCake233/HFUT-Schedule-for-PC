package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.AlumniScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object AlumniDestination : NavDestination() {
    override val key = "alumni"
    override val title = res(Strings.navigation_label_alumni)
    override val icon = Drawables.person_book

    @Composable
    override fun Content() {
        AlumniScreen()
    }
}