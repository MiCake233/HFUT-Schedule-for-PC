package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object NotificationsDestination : NavDestination() {
    override val key = "notification_center"
    override val title = res(Strings.navigation_label_notifications)
    override val icon = Drawables.notifications

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("notification_center")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}