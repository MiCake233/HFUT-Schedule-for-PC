package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class TransferMajorDetailDestination(
    val name : String,
    val batchId : String,
    val isHidden : Boolean
) : NavDestination() {
    override val key = "transfer_major_detail_${batchId}_${isHidden}"
    override val description = name
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(Strings.navigation_label_transfer_major_detail)
        val ICON = Drawables.compare_arrows
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("transfer_major_detail")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}