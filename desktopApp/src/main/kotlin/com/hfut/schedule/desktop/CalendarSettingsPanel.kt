package com.hfut.schedule.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 课表外观设置面板。
 *
 * Android 端该面板由 `CalendarUISettings` 实现，其中壁纸选取依赖相册接口；
 * 桌面端复用同一组 DataStore 偏好键，提供可在 PC 上生效的项目。
 */
@Composable
fun CalendarSettingsPanel(innerPadding: PaddingValues) {
    val scope = rememberCoroutineScope()

    val squareHeight by DataStoreManager.calendarSquareHeight.collectAsState(initial = com.hfut.schedule.logic.model.AppConst.CALENDAR_SQUARE_HEIGHT)
    val textSize by DataStoreManager.calendarSquareTextSize.collectAsState(initial = 1f)
    val textPadding by DataStoreManager.calendarSquareTextPadding.collectAsState(initial = com.hfut.schedule.logic.model.AppConst.CALENDAR_SQUARE_TEXT_PADDING)
    val squareAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = com.hfut.schedule.logic.model.AppConst.CALENDAR_SQUARE_ALPHA)
    val squareBlur by DataStoreManager.customCalendarSquareBlur.collectAsState(initial = com.hfut.schedule.logic.model.AppConst.CALENDAR_SQUARE_BLUR)
    val showTimeLine by DataStoreManager.enableShowCalendarTimeLine.collectAsState(initial = false)

    fun save(block: suspend () -> Unit) {
        scope.launch(Dispatchers.IO) { runCatching { block() }.onFailure { showToast("保存失败：${it.message}") } }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            DividerTextExpandedWith("课表方格") {
                CustomCard {
                    SliderRow(
                        title = "方格高度",
                        value = squareHeight,
                        valueText = { "${it.roundToInt()} dp" },
                        range = 48f..200f,
                        steps = 0,
                        onValueChange = { save { DataStoreManager.saveCalendarSquareHeight(it) } }
                    )
                    HorizontalDivider()
                    SliderRow(
                        title = "文字大小",
                        value = textSize,
                        valueText = { "%.2f×".format(it) },
                        range = 0.6f..1.6f,
                        steps = 9,
                        onValueChange = { save { DataStoreManager.saveCalendarSquareTextSize(it) } }
                    )
                    HorizontalDivider()
                    SliderRow(
                        title = "文字内边距",
                        value = textPadding,
                        valueText = { "%.1f".format(it) },
                        range = 0f..12f,
                        steps = 0,
                        onValueChange = { save { DataStoreManager.saveCalendarSquareTextPadding(it) } }
                    )
                }
            }
            DividerTextExpandedWith("背景与效果") {
                CustomCard {
                    SliderRow(
                        title = "方格不透明度",
                        value = squareAlpha,
                        valueText = { "%.2f".format(it) },
                        range = 0f..1f,
                        steps = 9,
                        onValueChange = { save { DataStoreManager.saveCustomSquareAlpha(it) } }
                    )
                    HorizontalDivider()
                    SliderRow(
                        title = "方格模糊",
                        value = squareBlur,
                        valueText = { "%.2f".format(it) },
                        range = 0f..1f,
                        steps = 9,
                        onValueChange = { save { DataStoreManager.saveCustomSquareBlur(it) } }
                    )
                    HorizontalDivider()
                    SettingSwitchRow(
                        title = "显示时间轴",
                        subtitle = if (showTimeLine) "课表左侧显示节次时间" else "课表左侧不显示时间",
                        checked = showTimeLine,
                        onCheckedChange = { checked ->
                            save { DataStoreManager.saveEnableSHowCalendarTimeLine(checked) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SliderRow(
    title: String,
    value: Float,
    valueText: (Float) -> String,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    // 拖动时只更新本地草稿值，松手才落盘：DataStore 每次写入都是整文件重写，
    // 逐帧写会把 IO 队列塞满，滑块也会因为读回来的值滞后而抖动。
    var draft by androidx.compose.runtime.remember(value, range) {
        androidx.compose.runtime.mutableStateOf(value.coerceIn(range.start, range.endInclusive))
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, modifier = Modifier.weight(1f))
            Text(valueText(draft), color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(
            value = draft.coerceIn(range.start, range.endInclusive),
            onValueChange = { draft = it },
            onValueChangeFinished = { onValueChange(draft.coerceIn(range.start, range.endInclusive)) },
            valueRange = range,
            steps = steps
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(painterResource(Drawables.schedule), contentDescription = null)
        Column(modifier = Modifier.weight(1f)) {
            Text(title)
            Text(
                subtitle,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
