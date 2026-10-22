package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res
// TODO
data class WebViewDestination(
    val url : String,
    val cookies : String?,
    val name : String,
    override val icon : Int = Drawables.net
) : NavDestination() {
    override val key = getKey(url)
    override val description = name
    override val title = res(Strings.navigation_label_web)

    companion object {
        fun getKey(url : String): String {
            return "web_${url}"
        }
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformDestination(this)) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}