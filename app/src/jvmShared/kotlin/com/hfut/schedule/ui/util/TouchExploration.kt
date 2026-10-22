package com.hfut.schedule.ui.util

import androidx.compose.runtime.Composable

/** 是否启用了读屏/触摸探索（TalkBack）。桌面端恒为 false。 */
@Composable
expect fun rememberTouchExplorationEnabled(): Boolean