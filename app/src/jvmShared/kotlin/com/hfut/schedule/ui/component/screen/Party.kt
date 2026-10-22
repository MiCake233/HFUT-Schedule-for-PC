package com.hfut.schedule.ui.component.screen

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

enum class PartyPlace {
    TOP_CENTER, CORNER, TOP_CORNER, CENTER, FULL
}

/**
 * 礼花特效。
 * Android 端用 Konfetti 库；桌面端用自研粒子动画（无第三方依赖）。
 */
@Composable
expect fun BoxScope.Party(
    timeSecond: Long = 500L,
    show: Boolean = timeSecond >= 500L,
    count: Int = 400,
    place: PartyPlace = PartyPlace.CORNER,
    modifier: Modifier = Modifier.zIndex(1f)
)
