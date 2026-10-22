package com.hfut.schedule.ui.component.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource

/**
 * 桌面端：没有 AVD 路径插值，直接用同一份向量图的静态形态。
 */
@Composable
actual fun rememberAnimatedNavIcon(iconId: Int, atEnd: Boolean): Painter =
    painterResource(if (iconId == ANIM_ICON_EXPAND) Drawables.expand_content else iconId)