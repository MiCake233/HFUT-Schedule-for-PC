package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object NotificationBoxDestination : NavDestination() {
    override val key = "notification_box"
    override val title = res(Strings.navigation_label_notification_box)
    override val icon = Drawables.notifications

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("notification_box")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}