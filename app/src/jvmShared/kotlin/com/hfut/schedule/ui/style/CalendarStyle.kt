package com.hfut.schedule.ui.style

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.xah.common.ui.style.APP_HORIZONTAL_DP


data class CalendarStyle(val showAll: Boolean) {
    val everyPadding = if (showAll) 1.dp else 1.75.dp
    val rowCount = if (showAll) 7 else 5
    val columnCount = 6

    /**
     * 课表（表头日期 + 网格）的最大宽度。
     *
     * 手机屏幕窄，5 列时每格约 70~80dp；桌面窗口动辄 1200dp 以上，
     * 同样 5 列会被拉成 200dp+，课程方块就显得又宽又胖。
     * 这里给课表区域一个统一上限并居中，让格子比例接近手机端。
     */
    val maxWidth = 560.dp

    val containerColor @Composable
    get() = MaterialTheme.colorScheme.surfaceContainerHigh.copy(.6f).compositeOver(MaterialTheme.colorScheme.surface)

    val containerCorner @Composable
    get() = MaterialTheme.shapes.extraSmall

    val calendarPaddingHorizontal = APP_HORIZONTAL_DP  - everyPadding * 2

    fun calendarPadding() = Modifier.padding(
        horizontal = calendarPaddingHorizontal,
        vertical = everyPadding
    )
}
