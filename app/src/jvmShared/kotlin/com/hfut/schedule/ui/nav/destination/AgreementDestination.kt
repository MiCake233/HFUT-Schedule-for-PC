package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

object AgreementDestination : NavDestination() {
    override val key: String = "agreement"
    override val title: UiText = res(Strings.navigation_label_agreement)
    override val icon = Drawables.partner_exchange

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("AgreementDestination")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}