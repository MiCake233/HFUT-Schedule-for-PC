package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.model.AppConst
import com.xah.shader.state.ShaderState

/**
 * 桌面端实现：没有 kyant backdrop 的折射透镜效果，
 * 用「半透明表面 + 圆角裁切」来近似液态玻璃观感，保留全部布局与交互行为。
 */
actual class PlatformBackdrop

@Composable
actual fun rememberLayerBackdrop(): PlatformBackdrop = remember { PlatformBackdrop() }

@Composable
actual fun LiquidButton(
    onClick: () -> Unit,
    backdrop: PlatformBackdrop,
    modifier: Modifier,
    enabled: Boolean,
    isCircle: Boolean,
    innerPadding: Dp,
    surfaceColor: Color,
    content: @Composable RowScope.() -> Unit
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = 14.5.sp,
        color = if (!enabled) Color.Gray else Color.Unspecified
    )
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = if (isCircle) CircleShape else ButtonDefaults.shape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = innerPadding),
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = surfaceColor.copy(if (enabled) .85f else .5f)),
    ) {
        CompositionLocalProvider(LocalTextStyle provides textStyle) {
            content()
        }
    }
}

@Composable
actual fun LiquidButton(
    onClick: () -> Unit,
    backdrop: PlatformBackdrop,
    modifier: Modifier,
    enabled: Boolean,
    isCircle: Boolean,
    shape: Shape,
    content: @Composable () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
    ) {
        CompositionLocalProvider(LocalContentColor provides IconButtonDefaults.iconButtonColors().contentColor) {
            content()
        }
    }
}

@Composable
actual fun Modifier.containerBackDrop(
    backdrop: PlatformBackdrop,
    shape: Shape,
    enabled: Boolean,
    surfaceColor: Color,
): Modifier {
    val color = if (surfaceColor.isSpecified) {
        surfaceColor
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(if (enabled) .55f else .8f)
    }
    return this
        .clip(shape)
        .background(color, shape)
}

@Composable
actual fun Modifier.containerBackDrop(
    backdrop: ShaderState,
    shape: Shape,
    surfaceColor: Color,
): Modifier = this
    .clip(shape)
    .background(surfaceColor, shape)

@Composable
actual fun Modifier.bottomBarBackDrop(
    backdrop: PlatformBackdrop,
    shape: RoundedCornerShape,
): Modifier = this
    .clip(shape)
    .background(MaterialTheme.colorScheme.surfaceVariant.copy(.75f), shape)

@Composable
actual fun Modifier.calendarSquareBackDrop(
    backdrop: PlatformBackdrop,
    source: CalendarSquareBackDropSource,
    shape: Shape,
    overlayColor: Color,
): Modifier {
    val alpha = when (source) {
        CalendarSquareBackDropSource.BUTTON -> AppConst.CALENDAR_SQUARE_ALPHA
        CalendarSquareBackDropSource.BOTTOM -> AppConst.CALENDAR_SQUARE_ALPHA
        CalendarSquareBackDropSource.SQUARE -> AppConst.CALENDAR_SQUARE_ALPHA
        CalendarSquareBackDropSource.CONTAINER -> (AppConst.CALENDAR_SQUARE_ALPHA + 0.3f).coerceAtMost(1f)
    }
    return this
        .clip(shape)
        .background(overlayColor.copy(alpha), shape)
}
