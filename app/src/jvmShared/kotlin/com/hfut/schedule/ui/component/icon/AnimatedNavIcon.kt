package com.hfut.schedule.ui.component.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * 底栏/页签的动画图标。
 *
 * Android 端用 AnimatedVectorDrawable 做路径插值动画；
 * Compose Multiplatform 没有 AVD 支持，桌面端退化为静态向量图（同一份资源）。
 * 两端都以 Compose Resources 注册表的下标（[com.xah.common.ui.res.Drawables]）作为入参。
 */
@Composable
expect fun rememberAnimatedNavIcon(iconId: Int, atEnd: Boolean): Painter
/** 特殊值：展开/收起动画图标（Android 用 AVD，桌面用静态 expand 图标） */
const val ANIM_ICON_EXPAND = -100