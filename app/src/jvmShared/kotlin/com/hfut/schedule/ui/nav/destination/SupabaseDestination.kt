package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

object SupabaseDestination : NavDestination() {
    override val key = "supabase_home"
    override val title = res(Strings.navigation_label_supabase)
    override val icon = Drawables.cloud

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("supabase_home")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}