package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class AiDestination(
    val keyword: String? = null,
) : NavDestination() {
    override val key = "${KEY}_$keyword"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "ai"
        val TITLE = res(Strings.navigation_label_ai)
        val ICON = Drawables.wand_stars
    }

    fun toDeepLink(): String = "hfut_schedule://$KEY?keyword=$keyword"

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformDestination(this)) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}
