package com.hfut.schedule.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 上课提醒 / 日程面板（原 `course_events_ui` 槽位）。
 *
 * Android 端该面板依赖 AlarmManager 与通知权限；桌面端由常驻协程 + 托盘气泡实现，
 * 这里提供提醒开关、提前分钟数与今日课程列表。
 */
@Composable
fun DesktopEventsPanel() {
    val scope = rememberCoroutineScope()
    val enabled by DataStoreManager.enableLiveCourseReminder.collectAsState(initial = false)
    val minutes by DataStoreManager.liveCourseReminderMinutes.collectAsState(initial = 20)
    var todayCourses by remember { mutableStateOf<List<TodayCourse>?>(null) }

    LaunchedEffect(Unit) { todayCourses = loadTodayCourses() }

    Column(modifier = Modifier.fillMaxWidth()) {
        DividerTextExpandedWith("提醒") {
            CustomCard {
                CardListItem(
                    headlineContent = { Text("上课与考试提醒") },
                    supportingContent = {
                        Text(if (enabled) "已开启，通过系统托盘气泡提醒" else "开启后按课表在课前提醒")
                    },
                    leadingContent = { Icon(painterResource(Drawables.schedule), null) },
                    trailingContent = {
                        Switch(
                            checked = enabled,
                            onCheckedChange = { checked ->
                                scope.launch(Dispatchers.IO) { DataStoreManager.saveLiveCourseReminder(checked) }
                            }
                        )
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("提前提醒时间")
                        Text(
                            "当前 $minutes 分钟",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FilledTonalIconButton(onClick = {
                        scope.launch(Dispatchers.IO) {
                            DataStoreManager.saveLiveCourseReminderMinutes((minutes - 5).coerceAtLeast(1))
                        }
                    }) { Icon(painterResource(Drawables.remove), contentDescription = "减少") }
                    FilledTonalIconButton(onClick = {
                        scope.launch(Dispatchers.IO) {
                            DataStoreManager.saveLiveCourseReminderMinutes((minutes + 5).coerceAtMost(120))
                        }
                    }) { Icon(painterResource(Drawables.add), contentDescription = "增加") }
                }
            }
        }
        DividerTextExpandedWith("今日课程") {
            CustomCard {
                val list = todayCourses
                when {
                    list == null -> Text(
                        "正在读取课表…",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    list.isEmpty() -> Text(
                        "今日无课",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    else -> Column(modifier = Modifier.fillMaxWidth()) {
                        list.forEach { course ->
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text("${course.start}–${course.end}", style = MaterialTheme.typography.labelMedium)
                                Text(course.name, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    listOfNotNull(course.place, course.teacher).joinToString(" · ").ifEmpty { "地点未登记" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
