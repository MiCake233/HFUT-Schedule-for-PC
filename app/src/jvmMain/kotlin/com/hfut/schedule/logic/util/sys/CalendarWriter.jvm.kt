package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 桌面端的「写入日历」。
 *
 * PC 上没有可写的系统日历（Windows 也没有统一的日历 API），
 * 因此改为导出 **.ics** 文件：写入 `下载` 目录下的 HFUT-Schedule.ics，
 * 用 Outlook / 系统日历 / 手机日历一键导入。
 *
 * 同一个文件会累积多条日程（按 UID 去重），重复点击不会产生重复事件。
 */
actual suspend fun addToCalendars(
    bean: DateTime,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?,
    remind: Boolean
) = withContext(Dispatchers.IO) {
    val start = runCatching { LocalDateTime.parse(bean.toString(), ICS_DT) }.getOrNull()
    if (start != null) {
        IcsExporter.append(
            IcsEvent(
                title = title,
                place = place,
                remark = remark,
                start = start,
                end = start.plusHours(1),
                remindMinutes = if (remind) 15 else null
            )
        )
    }
    Unit
}

actual suspend fun addToCalendars(
    startDate: String,
    startTime: String,
    endDate: String,
    endTime: String,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?
) = withContext(Dispatchers.IO) {
    val start = parse(startDate, startTime)
    val end = parse(endDate, endTime)
    if (start != null && end != null) {
        IcsExporter.append(
            IcsEvent(title = title, place = place, remark = remark, start = start, end = end)
        )
    }
    Unit
}

actual suspend fun addToCalendars(
    start: List<Int>,
    end: List<Int>,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?,
    remind: Boolean
) = withContext(Dispatchers.IO) {
    val startTime = parse(start)
    val endTime = parse(end)
    if (startTime != null && endTime != null) {
        IcsExporter.append(
            IcsEvent(
                title = title,
                place = place,
                remark = remark,
                start = startTime,
                end = endTime,
                remindMinutes = if (remind) 15 else null
            )
        )
    }
    Unit
}

private val ICS_DT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

/** iCalendar 要求用 CRLF 断行。只写 LF 时，部分日历（Outlook / Google）会拒绝导入。 */
private const val ICS_EOL = "\r\n"

/**
 * 转义 TEXT 值。回车也要处理：只替换 `\n` 时，标题里的 `\r` 可以另起一行注入 `BEGIN:VEVENT`。
 */
fun escapeIcsText(value: String): String = value
    .replace("\\", "\\\\")
    .replace("\r\n", "\\n")
    .replace("\n", "\\n")
    .replace("\r", "\\n")
    .replace(",", "\\,")
    .replace(";", "\\;")

private fun parse(date: String, time: String): LocalDateTime? = runCatching {
    LocalDateTime.parse("$date $time", ICS_DT)
}.getOrNull()

private fun parse(parts: List<Int>): LocalDateTime? = runCatching {
    LocalDateTime.of(
        parts[0],
        parts[1],
        parts[2],
        parts.getOrElse(3) { 0 },
        parts.getOrElse(4) { 0 }
    )
}.getOrNull()

/** 一条待导出的日程 */
internal data class IcsEvent(
    val title: String,
    val place: String?,
    val remark: String?,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val remindMinutes: Int? = null
)

/**
 * 极简 iCalendar 导出器：向 `下载/HFUT-Schedule.ics` 追加 VEVENT。
 * 只依赖 java.time，不引入第三方库。
 */
internal object IcsExporter {

    private val dtFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    private val stampFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

    /**
     * iCalendar 的 `DTSTAMP` 必须是 **UTC** 时间（带 Z 后缀）。
     * 早先这里用的是本地时间 + 字面量 Z，导出的时间戳会整体偏 8 小时，
     * 严格的解析器（Outlook / Google Calendar）会认为事件元数据错乱。
     */
    private fun utcStamp(): String =
        java.time.Instant.now().atZone(java.time.ZoneOffset.UTC).format(stampFormat)

    private fun targetFile(): File {
        val downloads = File(System.getProperty("user.home"), "Downloads")
        val dir = if (downloads.isDirectory) downloads else File(System.getProperty("user.home"))
        dir.mkdirs()
        return File(dir, "HFUT-Schedule.ics")
    }

    fun append(event: IcsEvent) {
        runCatching {
            val file = targetFile()
            val existing = if (file.exists()) file.readText() else ""
            val body = stripTrailer(existing)
            val events = body.ifEmpty { header() }.trimEnd()
            val uid = "hfut-" + event.start.toString().hashCode().toUInt().toString(16) +
                "-" + event.title.hashCode().toUInt().toString(16) + "@hfut-schedule"
            if (events.contains(uid)) {
                // 已存在同一条日程
                com.hfut.schedule.logic.util.sys.showToast("日程已在导出文件中：${file.absolutePath}")
                return@runCatching
            }
            val sb = StringBuilder(events)
            sb.append(ICS_EOL)
            sb.append("BEGIN:VEVENT").append(ICS_EOL)
            sb.append("UID:").append(uid).append(ICS_EOL)
            sb.append("DTSTAMP:").append(utcStamp()).append(ICS_EOL)
            sb.append("DTSTART:").append(event.start.format(dtFormat)).append(ICS_EOL)
            sb.append("DTEND:").append(event.end.format(dtFormat)).append(ICS_EOL)
            sb.append("SUMMARY:").append(escapeIcsText(event.title)).append(ICS_EOL)
            event.place?.takeIf { it.isNotBlank() }?.let {
                sb.append("LOCATION:").append(escapeIcsText(it)).append(ICS_EOL)
            }
            event.remark?.takeIf { it.isNotBlank() }?.let {
                sb.append("DESCRIPTION:").append(escapeIcsText(it)).append(ICS_EOL)
            }
            event.remindMinutes?.let {
                sb.append("BEGIN:VALARM").append(ICS_EOL)
                sb.append("TRIGGER:-PT").append(it).append('M').append(ICS_EOL)
                sb.append("ACTION:DISPLAY").append(ICS_EOL)
                sb.append("DESCRIPTION:").append(escapeIcsText(event.title)).append(ICS_EOL)
                sb.append("END:VALARM").append(ICS_EOL)
            }
            sb.append("END:VEVENT").append(ICS_EOL)
            sb.append("END:VCALENDAR").append(ICS_EOL)
            file.writeText(sb.toString())
            com.hfut.schedule.logic.util.sys.showToast("已导出到 ${file.absolutePath}")
        }
    }

    private fun header(): String = buildString {
        append("BEGIN:VCALENDAR").append(ICS_EOL)
        append("VERSION:2.0").append(ICS_EOL)
        append("PRODID:-//HFUT-Schedule//Desktop//CN").append(ICS_EOL)
        append("CALSCALE:GREGORIAN").append(ICS_EOL)
        append("METHOD:PUBLISH").append(ICS_EOL)
    }

    private fun stripTrailer(text: String): String =
        text.substringBefore("END:VCALENDAR")

    /** 供「导出当前课表」按钮使用：一次性写入多个事件 */
    fun exportAll(events: List<IcsEvent>) {
        val file = targetFile()
        val sb = StringBuilder(header())
        events.forEach { event ->
            val uid = "hfut-" + event.start.toString().hashCode().toUInt().toString(16) +
                "-" + event.title.hashCode().toUInt().toString(16) + "@hfut-schedule"
            sb.append("BEGIN:VEVENT").append(ICS_EOL)
            sb.append("UID:").append(uid).append(ICS_EOL)
            sb.append("DTSTAMP:").append(utcStamp()).append(ICS_EOL)
            sb.append("DTSTART:").append(event.start.format(dtFormat)).append(ICS_EOL)
            sb.append("DTEND:").append(event.end.format(dtFormat)).append(ICS_EOL)
            sb.append("SUMMARY:").append(escapeIcsText(event.title)).append(ICS_EOL)
            event.place?.takeIf { it.isNotBlank() }?.let { sb.append("LOCATION:").append(escapeIcsText(it)).append(ICS_EOL) }
            event.remark?.takeIf { it.isNotBlank() }?.let { sb.append("DESCRIPTION:").append(escapeIcsText(it)).append(ICS_EOL) }
            sb.append("END:VEVENT").append(ICS_EOL)
        }
        sb.append("END:VCALENDAR").append(ICS_EOL)
        runCatching {
            file.writeText(sb.toString())
            com.hfut.schedule.logic.util.sys.showToast("已导出到 ${file.absolutePath}")
        }
    }
}
