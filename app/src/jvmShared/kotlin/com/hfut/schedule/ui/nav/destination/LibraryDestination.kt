package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object LibraryDestination : NavDestination() {
    override val key = "library"
    override val title = res(Strings.navigation_label_library)
    override val icon = Drawables.book_5

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("library")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}