package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Drawables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.util.text

data class AppointmentSeatDestination(
    val communityId: String,
    val desc: String
) : NavDestination() {
    override val key = "${KEY}_$communityId"
    override val title = text(desc)
    override val icon = ICON

    companion object {
        const val KEY = "appointment_seat"
        val ICON = Drawables.table_restaurant
    }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            CenterScreen {
                DevelopingIcon()
            }
        }
    }
}
