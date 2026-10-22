package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.cube.sub.CornerSettingsScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res
import com.xah.common.ui.util.text

data object CornerSettingsDestination : NavDestination() {

    override val title = res(Strings.navigation_label_settings_corner)
    override val key = "settings_corner"
    override val icon = Drawables.rounded_corner

    @Composable
    override fun Content() {
        CornerSettingsScreen()
    }
}