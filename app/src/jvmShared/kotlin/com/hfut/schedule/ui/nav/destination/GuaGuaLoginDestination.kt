package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object GuaGuaLoginDestination : NavDestination() {
    override val key = "guagua_login"
    override val title = text("呱呱物联-登录")
    override val icon = Drawables.login

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("guagua_login")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}