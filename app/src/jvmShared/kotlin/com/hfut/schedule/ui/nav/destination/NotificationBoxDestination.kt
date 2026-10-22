package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object NotificationBoxDestination : NavDestination() {
    override val key = "notification_box"
    override val title = res(Strings.navigation_label_notification_box)
    override val icon = Drawables.notifications

    @Composable
    override fun Content() {
        NotificationBoxScreen()
    }
}