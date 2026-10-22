package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.shader.state.ShaderState

/**
 * 液态玻璃按钮 / 背景的跨平台 API。
 *
 * Android 端由 kyant backdrop 库 + RuntimeShader 实现（见 androidMain）；
 * 桌面端退化为半透明表面 + Haze 模糊（见 jvmMain）——
 * 视觉细节有差异，但布局、交互、圆角、按压反馈保持一致。
 */
val BUTTON_PADDING = 6.25.dp

val BUTTON_END_PADDING = APP_HORIZONTAL_DP - CARD_NORMAL_DP - 1.dp

@Composable
fun NoPadding(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp
    ) {
        content()
    }
}

enum class CalendarSquareBackDropSource {
    BUTTON, BOTTOM, SQUARE, CONTAINER
}

@Composable
expect fun LiquidButton(
    onClick: () -> Unit,
    backdrop: PlatformBackdrop,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isCircle: Boolean = false,
    innerPadding: Dp = if (!isCircle) 20.dp else 9.5.dp,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable RowScope.() -> Unit
)

@Composable
expect fun LiquidButton(
    onClick: () -> Unit,
    backdrop: PlatformBackdrop,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isCircle: Boolean = false,
    shape: Shape,
    content: @Composable () -> Unit
)

@Composable
expect fun Modifier.containerBackDrop(
    backdrop: PlatformBackdrop,
    shape: Shape,
    enabled: Boolean = true,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(if (enabled) .3f else .7f),
): Modifier

@Composable
expect fun Modifier.containerBackDrop(
    backdrop: ShaderState,
    shape: Shape,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(.7f),
): Modifier

@Composable
expect fun Modifier.bottomBarBackDrop(
    backdrop: PlatformBackdrop,
    shape: RoundedCornerShape = CircleShape,
): Modifier

@Composable
expect fun Modifier.calendarSquareBackDrop(
    backdrop: PlatformBackdrop,
    source: CalendarSquareBackDropSource,
    shape: Shape = CircleShape,
    overlayColor: Color = MaterialTheme.colorScheme.surface,
): Modifier
