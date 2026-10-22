package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.AIScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object AiDestination : NavDestination() {
    override val key = "ai"
    override val title = res(Strings.navigation_label_ai)
    override val icon = Drawables.wand_stars

    @Composable
    override fun Content() {
        AIScreen()
    }
}