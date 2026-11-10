package com.hfut.schedule.ui.util.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.model.UiWidthSize

/**
 * 宽度档位与 Material 窗口尺寸类一致（600dp / 840dp）。
 *
 * 上游用 `androidx.compose.material3.adaptive`，那个坐标在桌面 JVM 上没有对应实现。
 * 这里直接读当前窗口宽度，Android 与桌面共用同一套断点。
 */
@Composable
fun currentUiWidthSize(): UiWidthSize {
    val widthPx = LocalWindowInfo.current.containerSize.width
    val width = with(LocalDensity.current) { widthPx.toDp() }
    return when {
        width >= 840.dp -> UiWidthSize.LARGE
        width >= 600.dp -> UiWidthSize.MEDIUM
        else -> UiWidthSize.SMALL
    }
}

@Composable
fun currentUiWidthSizeColumnsForCard(): Int = when (currentUiWidthSize()) {
    UiWidthSize.SMALL -> 1
    UiWidthSize.MEDIUM -> 1
    UiWidthSize.LARGE -> 2
}

@Composable
fun currentUiWidthSizeColumnsForSmallCard(): Int = when (currentUiWidthSize()) {
    UiWidthSize.SMALL -> 2
    UiWidthSize.MEDIUM -> 3
    UiWidthSize.LARGE -> 4
}
