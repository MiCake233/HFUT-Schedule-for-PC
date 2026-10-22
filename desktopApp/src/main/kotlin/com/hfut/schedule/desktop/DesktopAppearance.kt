package com.hfut.schedule.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.model.ColorStyle
import com.hfut.schedule.ui.model.choice.ColorMode
import com.hfut.schedule.ui.nav.destination.SettingsAppearancePlatformDestination
import com.xah.common.ui.model.text.asStringNow
import kotlinx.coroutines.launch

/** 桌面端可选的主题种子色（工大红 + 几个常用色） */
private val seedColors = listOf(
    "工大红" to 0xFFB3121F,
    "朱砂" to 0xFFC0392B,
    "靛蓝" to 0xFF1F4E9C,
    "青竹" to 0xFF2E7D5B,
    "紫藤" to 0xFF6A3FA0,
    "琥珀" to 0xFFB07B1E,
)

private val colorModes = listOf(
    ColorMode.AUTO to "跟随系统",
    ColorMode.LIGHT to "浅色",
    ColorMode.DARK to "深色",
)

/**
 * 桌面版「外观设置」页。
 *
 * Android 端的外观设置页塞满了壁纸取色、小组件、共享容器调试等系统相关内容；
 * 桌面端这一页只保留真正有意义的几项，全部直接读写共享的 DataStore，
 * 与手机端的设置互通。
 */
@Composable
fun DesktopAppearanceScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val colorMode by DataStoreManager.colorMode.collectAsState(initial = ColorMode.AUTO.code)
    val customColor by DataStoreManager.customColor.collectAsState(initial = -1L)
    val customColorStyle by DataStoreManager.customColorStyle.collectAsState(initial = ColorStyle.DEFAULT.code)
    val usePureBlack by DataStoreManager.enablePureDark.collectAsState(initial = false)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = true)
    val enableHaze by DataStoreManager.enableHazeBlur.collectAsState(initial = true)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                title = { Text(SettingsAppearancePlatformDestination.title.asStringNow()) },
                navigationIcon = {
                    Icon(
                        painter = com.xah.common.ui.res.painterResource(com.xah.common.ui.res.Drawables.arrow_back),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp).size(24.dp).clickable { onBack() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 深浅色
            SectionCard("深色模式") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorModes.forEach { (mode, label) ->
                        val selected = colorMode == mode.code
                        SelectableChip(text = label, selected = selected) {
                            scope.launch { DataStoreManager.saveColorMode(mode) }
                        }
                    }
                }
            }

            // 纯黑
            SectionCard("纯黑背景") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("OLED 风格纯黑（深色模式更省电）", modifier = Modifier.weight(1f))
                    Switch(
                        checked = usePureBlack,
                        onCheckedChange = { on -> scope.launch { DataStoreManager.savePureDark(on) } }
                    )
                }
            }

            // 主题色
            SectionCard("主题色") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    seedColors.forEach { (label, argb) ->
                        val selected = customColor == argb.toLong() || (customColor == -1L && argb == 0xFFB3121F)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(argb))
                                .clickable { scope.launch { DataStoreManager.saveCustomColor(argb.toLong()) } }
                        ) {
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )
                            }
                        }
                    }
                    SelectableChip(text = "恢复默认", selected = customColor == -1L) {
                        scope.launch { DataStoreManager.saveCustomColor(-1L) }
                    }
                }
            }

            // 配色风格
            SectionCard("配色风格") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorStyle.entries.forEach { style ->
                        SelectableChip(
                            text = style.description.asStringNow() ?: style.name,
                            selected = customColorStyle == style.code
                        ) {
                            scope.launch { DataStoreManager.saveCustomColorStyle(style) }
                        }
                    }
                }
            }

            // 模糊 / 液态玻璃
            SectionCard("视觉特效") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("液态玻璃（着色器）", modifier = Modifier.weight(1f))
                    Switch(
                        checked = enableLiquidGlass,
                        onCheckedChange = { on -> scope.launch { DataStoreManager.saveLiquidGlass(on) } }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("模糊效果", modifier = Modifier.weight(1f))
                    Switch(
                        checked = enableHaze,
                        onCheckedChange = { on -> scope.launch { DataStoreManager.saveHazeBlur(on) } }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        content()
    }
}

@Composable
private fun SelectableChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelLarge)
    }
}
