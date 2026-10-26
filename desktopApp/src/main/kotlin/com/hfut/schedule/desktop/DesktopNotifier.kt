package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.DateTimeBean
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import com.hfut.schedule.ui.screen.home.calendar.common.examToCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/** 桌面通知出口：由窗口层接到系统托盘气泡。 */
object DesktopNotifier {
    var sink: ((title: String, message: String) -> Unit)? = null

    fun send(title: String, message: String) {
        runCatching { sink?.invoke(title, message) }
    }
}

/**
 * 上课与考试提醒。
 *
 * 每分钟检查一次本地课表与考试数据，在「开始时间 − 提前分钟数」之后、开始时间之前提醒一次。
 * 提前分钟数与开关复用 Android 端的同一组偏好键。
 */
object DesktopReminders {

    private val notified = mutableSetOf<String>()
    private var lastRefresh = 0L
    private var courses: List<ReminderItem> = emptyList()
    private var exams: List<ReminderItem> = emptyList()

    private data class ReminderItem(
        val title: String,
        val place: String?,
        val teacher: String?,
        val start: LocalDateTime,
        val end: LocalDateTime,
        val type: String,
    )

    fun start(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                runCatching { tick() }
                delay(60_000)
            }
        }
    }

    private suspend fun tick() {
        if (!DataStoreManager.enableLiveCourseReminder.first()) return
        val minutes = DataStoreManager.liveCourseReminderMinutes.first().coerceIn(1, 120)
        val now = LocalDateTime.now()

        if (System.currentTimeMillis() - lastRefresh > 10 * 60_000L) {
            refresh()
            lastRefresh = System.currentTimeMillis()
        }

        for (item in courses + exams) {
            val remindAt = item.start.minusMinutes(minutes.toLong())
            if (now.isBefore(remindAt) || !now.isBefore(item.start)) continue
            if (!notified.add("${item.type}|${item.title}|${item.start}")) continue

            val time = item.start.toLocalTime().toString().take(5)
            val place = item.place?.takeIf { it.isNotBlank() }
            val body = buildString {
                append(time)
                if (place != null) append("，").append(place)
                append("，").append(minutes).append(" 分钟后开始")
            }
            DesktopNotifier.send("${item.type}提醒：${item.title}", body)
        }

        // 去重键里带开始时间（含日期），把非今天的记录清掉即可避免长期运行后集合无限增长
        val today = LocalDate.now().toString()
        notified.removeAll { !it.contains(today) }
    }

    private suspend fun refresh() {
        courses = runCatching {
            getJxglstuCourseSchedule()
                .filter { it.time.start.year > 2000 }
                .map { course ->
                    ReminderItem(
                        title = course.courseName,
                        place = course.place,
                        teacher = course.teacher,
                        start = toLocalDateTime(course.time.start),
                        end = toLocalDateTime(course.time.end),
                        type = "上课",
                    )
                }
        }.getOrDefault(emptyList())

        exams = runCatching {
            examToCalendar().mapNotNull { exam ->
                val day = exam.day ?: return@mapNotNull null
                val startTime = exam.startTime ?: return@mapNotNull null
                val endTime = exam.endTime ?: return@mapNotNull null
                val title = exam.course ?: return@mapNotNull null
                val start = parseDateTime(day, startTime) ?: return@mapNotNull null
                val end = parseDateTime(day, endTime) ?: return@mapNotNull null
                ReminderItem(title, exam.place, exam.type, start, end, "考试")
            }
        }.getOrDefault(emptyList())
    }

    private fun toLocalDateTime(bean: DateTimeBean): LocalDateTime =
        LocalDateTime.of(bean.year, bean.month, bean.day, bean.hour, bean.minute)

    private fun parseDateTime(date: String, time: String): LocalDateTime? = runCatching {
        val d = date.split("-").map { it.toInt() }
        val t = time.split(":").map { it.toInt() }
        if (d.size != 3 || t.size != 2) null else LocalDateTime.of(d[0], d[1], d[2], t[0], t[1])
    }.getOrNull()

    /** 今日课程，供设置页展示提醒状态 */
    fun todayCourses(): List<String> {
        val today = LocalDate.now()
        return courses.filter { it.start.toLocalDate() == today }
            .sortedBy { it.start }
            .map { "${it.start.toLocalTime().toString().take(5)} ${it.title}" }
    }
}
