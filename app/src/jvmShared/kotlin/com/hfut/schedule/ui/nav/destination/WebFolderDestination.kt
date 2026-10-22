package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebNavigationScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object WebFolderDestination : NavDestination() {
    override val key = "web_folder"
    override val title = res(Strings.navigation_label_web_folder)
    override val icon = Drawables.explore

    @Composable
    override fun Content() {
        WebNavigationScreen()
    }
}