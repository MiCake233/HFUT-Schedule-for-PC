package com.hfut.schedule.desktop

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.prefs.Preferences

/** 桌面课表小组件：常驻小窗口，显示今日课程（时间 / 课程名 / 地点）。 */
object DesktopWidgetState {
    private val prefs: Preferences = Preferences.userRoot().node("com/hfut/schedule/desktop")

    var visible by mutableStateOf(prefs.getBoolean("widget.visible", false))
        private set
    var alwaysOnTop by mutableStateOf(prefs.getBoolean("widget.alwaysOnTop", true))
        private set

    fun setShown(value: Boolean) {
        visible = value
        prefs.putBoolean("widget.visible", value)
        runCatching { prefs.flush() }
    }

    fun setOnTop(value: Boolean) {
        alwaysOnTop = value
        prefs.putBoolean("widget.alwaysOnTop", value)
        runCatching { prefs.flush() }
    }
}

internal data class TodayCourse(
    val start: String,
    val end: String,
    val name: String,
    val place: String?,
    val teacher: String?,
)

/** 读取今日课程；与提醒功能共用同一份本地数据源。 */
internal suspend fun loadTodayCourses(): List<TodayCourse> = withContext(Dispatchers.IO) {
    runCatching {
        val today = LocalDate.now()
        getJxglstuCourseSchedule()
            .filter { it.time.start.year > 2000 }
            .filter {
                runCatching {
                    LocalDate.of(it.time.start.year, it.time.start.month, it.time.start.day) == today
                }.getOrDefault(false)
            }
            .map {
                TodayCourse(
                    start = formatTime(it.time.start.hour, it.time.start.minute),
                    end = formatTime(it.time.end.hour, it.time.end.minute),
                    name = it.courseName,
                    place = it.place,
                    teacher = it.teacher,
                )
            }
            .sortedBy { it.start }
    }.getOrDefault(emptyList())
}

private fun formatTime(hour: Int, minute: Int): String =
    "%02d:%02d".format(hour, minute)

@Composable
fun TodayCourseWidget(onClose: () -> Unit) {
    var courses by remember { mutableStateOf<List<TodayCourse>?>(null) }
    var refreshing by remember { mutableStateOf(false) }
    var tick by remember { mutableStateOf(0) }

    LaunchedEffect(tick) {
        refreshing = true
        courses = loadTodayCourses()
        refreshing = false
    }
    // 每 5 分钟自动刷新
    LaunchedEffect(Unit) {
        while (true) {
            delay(5 * 60_000L)
            tick++
        }
    }

    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("今日课程", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd")) +
                            " · " + weekdayText(LocalDate.now().dayOfWeek.value),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (refreshing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                }
                IconButton(onClick = { tick++ }, modifier = Modifier.size(32.dp)) {
                    Icon(painterResource(Drawables.rotate_right), contentDescription = "刷新")
                }
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(painterResource(Drawables.close), contentDescription = "关闭")
                }
            }
            Spacer(Modifier.height(6.dp))
            val list = courses
            when {
                list == null -> PlaceholderText("正在读取课表…")
                list.isEmpty() -> PlaceholderText("今日无课")
                else -> Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    list.forEach { course ->
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = course.start,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(46.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(course.name, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = listOfNotNull(
                                        course.place?.takeIf { it.isNotBlank() },
                                        course.teacher?.takeIf { it.isNotBlank() }
                                    ).joinToString(" · ").ifEmpty { "地点未登记" },
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

@Composable
private fun PlaceholderText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun weekdayText(dayOfWeek: Int): String =
    when (dayOfWeek) {
        1 -> "周一"
        2 -> "周二"
        3 -> "周三"
        4 -> "周四"
        5 -> "周五"
        6 -> "周六"
        else -> "周日"
    }
