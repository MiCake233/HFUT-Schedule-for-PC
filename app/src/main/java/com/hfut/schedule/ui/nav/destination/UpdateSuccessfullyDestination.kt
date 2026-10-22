package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.welcome.UpdateSuccessScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

object UpdateSuccessfullyDestination : NavDestination() {
    override val key: String = "update_successfully"
    override val title: UiText = res(Strings.navigation_label_update_successfully)
    override val icon = Drawables.settings

    @Composable
    override fun Content() {
        UpdateSuccessScreen()
    }
}