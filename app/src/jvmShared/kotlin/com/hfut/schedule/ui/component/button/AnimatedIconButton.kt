package com.hfut.schedule.ui.component.button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedExpandIconButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = Color.Transparent,
    valueState: Boolean,
    onClick: (() -> Unit)? = null
) {
    val painter = com.hfut.schedule.ui.component.icon.rememberAnimatedNavIcon(
        com.hfut.schedule.ui.component.icon.ANIM_ICON_EXPAND,
        valueState
    )

    IconButton(
        onClick = onClick ?: {},
        enabled = onClick != null, // 没传就是禁用
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor
        ) // 可传入背景颜色
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
fun AnimatedExpandLiquidIconButton(
    backdrop: PlatformBackdrop,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    valueState: Boolean,
    onClick: (() -> Unit)? = null
) {
    val painter = com.hfut.schedule.ui.component.icon.rememberAnimatedNavIcon(
        com.hfut.schedule.ui.component.icon.ANIM_ICON_EXPAND,
        valueState
    )

    LiquidButton(
        backdrop = backdrop,
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        isCircle = true
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint
        )
    }
}
