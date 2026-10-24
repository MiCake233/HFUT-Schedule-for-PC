package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Color
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.RRect
import org.jetbrains.skia.Surface
import org.jetbrains.skia.Typeface
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** 把当前周课表渲染为 PNG 图片。 */
object TimetableImageExporter {

    private val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

    private data class Cell(val day: Int, val start: String, val end: String, val name: String, val place: String)

    private val palette: List<Int> = listOf(
        0xFFB3121F.toInt(), 0xFF1F4E9C.toInt(), 0xFF2E7D5B.toInt(), 0xFFB07B1E.toInt(), 0xFF6A3FA0.toInt(), 0xFF0F766E.toInt(), 0xFF9A3412.toInt()
    )

    suspend fun export(): File? = withContext(Dispatchers.IO) {
        runCatching {
            val week = com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.currentWeek
            val termStart = LocalDate.parse(DataStoreManager.termStartDate.first())
            val weekStart = termStart.plusWeeks((week - 1).coerceAtLeast(0))
            val weekEnd = weekStart.plusDays(6)

            val courses = getJxglstuCourseSchedule()
                .filter { it.time.start.year > 2000 }
                .mapNotNull { course ->
                    val date = runCatching {
                        LocalDate.of(course.time.start.year, course.time.start.month, course.time.start.day)
                    }.getOrNull() ?: return@mapNotNull null
                    if (date.isBefore(weekStart) || date.isAfter(weekEnd)) return@mapNotNull null
                    val day = date.dayOfWeek.value - 1
                    Cell(
                        day = day,
                        start = "%02d:%02d".format(course.time.start.hour, course.time.start.minute),
                        end = "%02d:%02d".format(course.time.end.hour, course.time.end.minute),
                        name = course.courseName,
                        place = course.place.orEmpty(),
                    )
                }
                .sortedWith(compareBy({ it.start }, { it.day }))

            val startTimes = courses.map { it.start }.distinct().sorted()
            if (startTimes.isEmpty()) return@runCatching null

            val colWidth = 168
            val leftWidth = 96
            val headerHeight = 96
            val rowHeight = 126
            val width = leftWidth + colWidth * 7 + 24
            val height = headerHeight + rowHeight * startTimes.size + 32

            val surface = Surface.makeRasterN32Premul(width, height)
            val canvas = surface.canvas
            canvas.clear(Color.WHITE)

            val fontMgr = org.jetbrains.skia.FontMgr.default
            val titleTypeface: Typeface? = fontMgr.matchFamilyStyle("Microsoft YaHei", org.jetbrains.skia.FontStyle.NORMAL)
                ?: fontMgr.matchFamilyStyle("SimHei", org.jetbrains.skia.FontStyle.NORMAL)
                ?: fontMgr.matchFamilyStyle("Arial", org.jetbrains.skia.FontStyle.NORMAL)
            val titleFont = Font(titleTypeface, 34f)
            val bodyFont = Font(titleTypeface, 20f)
            val smallFont = Font(titleTypeface, 17f)
            val labelFont = Font(titleTypeface, 20f)

            val title = "第${week}周课表  ${weekStart.format(DateTimeFormatter.ofPattern("MM-dd"))} ~ ${weekEnd.format(DateTimeFormatter.ofPattern("MM-dd"))}"
            val textPaint = Paint().apply { color = Color.makeRGB(30, 30, 30) }
            canvas.drawString(title, 24f, 52f, titleFont, textPaint)

            val gridPaint = Paint().apply {
                color = Color.makeRGB(210, 210, 210)
                mode = PaintMode.STROKE
                strokeWidth = 1.5f
            }

            // 表头
            for (i in weekdays.indices) {
                val x = leftWidth + i * colWidth
                canvas.drawString(weekdays[i], x + 16f, headerHeight - 30f, labelFont, textPaint)
            }
            canvas.drawString("时间", 24f, headerHeight - 30f, labelFont, textPaint)

            // 网格
            for (r in 0..startTimes.size) {
                val y = (headerHeight + r * rowHeight).toFloat()
                canvas.drawLine(12f, y, (width - 12).toFloat(), y, gridPaint)
            }
            for (c in 0..7) {
                val x = (leftWidth + c * colWidth).toFloat()
                canvas.drawLine(x, (headerHeight - 46).toFloat(), x, (height - 16).toFloat(), gridPaint)
            }

            // 时间列
            startTimes.forEachIndexed { index, time ->
                val y = headerHeight + index * rowHeight + rowHeight / 2f + 6f
                canvas.drawString(time, 24f, y, smallFont, textPaint)
            }

            // 课程块
            courses.forEachIndexed { index, cell ->
                val rowIndex = startTimes.indexOf(cell.start)
                if (rowIndex < 0) return@forEachIndexed
                val span = startTimes.count { it >= cell.start && it < cell.end }.coerceAtLeast(1)
                val x = (leftWidth + cell.day * colWidth + 4).toFloat()
                val y = (headerHeight + rowIndex * rowHeight + 4).toFloat()
                val w = (colWidth - 8).toFloat()
                val h = (rowHeight * span - 8).toFloat()

                val color = palette[index % palette.size]
                val fill = Paint().apply {
                    this.color = Color.makeARGB(56, ((color shr 16).toInt() and 0xFF), ((color shr 8).toInt() and 0xFF), (color.toInt() and 0xFF))
                    mode = PaintMode.FILL
                }
                canvas.drawRRect(RRect.makeXYWH(x, y, w, h, 10f), fill)

                val accent = Paint().apply {
                    this.color = Color.makeRGB(((color shr 16).toInt() and 0xFF), ((color shr 8).toInt() and 0xFF), (color.toInt() and 0xFF))
                    mode = PaintMode.FILL
                }
                canvas.drawRRect(RRect.makeXYWH(x, y, 6f, h, 3f), accent)

                val namePaint = Paint().apply { this.color = Color.makeRGB(24, 24, 24) }
                canvas.drawString(trim(cell.name, 9), x + 16f, y + 30f, bodyFont, namePaint)
                if (cell.place.isNotEmpty()) {
                    val placePaint = Paint().apply { this.color = Color.makeRGB(90, 90, 90) }
                    canvas.drawString(trim(cell.place, 11), x + 16f, y + 58f, smallFont, placePaint)
                }
            }

            val image = surface.makeImageSnapshot()
            val data = image.encodeToData(EncodedImageFormat.PNG) ?: return@runCatching null
            val downloads = File(System.getProperty("user.home"), "Downloads").let {
                if (it.isDirectory) it else File(System.getProperty("user.home"))
            }
            val out = File(downloads, "HFUT-Schedule-第${week}周课表.png")
            out.writeBytes(data.bytes)
            surface.close()
            out
        }.getOrNull()
    }

    private fun trim(text: String, max: Int): String =
        if (text.length <= max) text else text.take(max) + "…"
}
