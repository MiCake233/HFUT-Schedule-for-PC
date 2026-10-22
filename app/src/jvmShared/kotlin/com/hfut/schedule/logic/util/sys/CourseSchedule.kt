package com.hfut.schedule.logic.util.sys
import com.xah.common.logic.util.LogUtil

import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuCourseTableResponse
import com.hfut.schedule.network.core.GsonInstance

/**
 * 课程日程的数据结构与解析。
 * 原先这些类混在 CalendarUtils.kt（系统日历写入，Android 专属）里，
 * 桌面端也要用，故抽到共享源集。
 */
enum class RepeatFrequency {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

internal fun parseDateTimeBean(start : List<Int>, end : List<Int>) : DateTime? =
    if(start.size == end.size && start.size == 5) {
        try {
            DateTime(
                DateTimeBean(start[0],start[1],start[2],start[3],start[4]),
                DateTimeBean(end[0],end[1],end[2],end[3],end[4])
            )
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    } else {
        null
    }

fun parseToDateTime(startDate: String,startTime: String,endDate: String,endTime: String) : DateTime? {
    val startBean = parseStrToDateTimeBean(startDate,startTime) ?: return null
    val endBean = parseStrToDateTimeBean(endDate,endTime) ?: return null
    return DateTime(startBean,endBean)
}

// 将时间转换为标准时间 例如东八区->0时区

fun parseStrToDateTimeBean(date: String, time: String) : DateTimeBean? {
    val dates = date.split("-")
    if(dates.size != 3) return null
    val year = dates[0].toIntOrNull() ?: return null
    val month = dates[1].toIntOrNull() ?: return null
    val day = dates[2].toIntOrNull() ?: return null
    val times = time.split(":")
    if(times.size != 2) return null
    val hour = times[0].toIntOrNull() ?: return null
    val minute = times[1].toIntOrNull() ?: return null
    return DateTimeBean(year,month,day,hour,minute)
}

data class JxglstuCourseSchedule(
    val time : DateTime,
    val place : String?,
    val courseName : String,
    val courseCode : String? = null,
    val teacher : String? = null,
)

suspend fun getJxglstuCourseSchedule(
    jsonStr : String? = null,
) : List<JxglstuCourseSchedule>  {
    val json = jsonStr ?: LargeStringDataManager.read(LargeStringDataManager.getJxglstuDatumKey(SemesterParser.getSemester()))
    if(json == null) {
        return emptyList()
    }

    val list = mutableListOf<JxglstuCourseSchedule>()
    try {
        val datumResponse = GsonInstance.fromJson(json, JxglstuCourseTableResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        for (i in scheduleList.indices) {
            val item = scheduleList[i]
            var startTime = item.startTime.toString()
            startTime = startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(startTime.length - 2)

            var endTime = item.endTime.toString()
            endTime = endTime.substring(0, endTime.length - 2) + ":" + endTime.substring(endTime.length - 2)
            val room = item.room?.nameZh
            var courseId = item.lessonId.toString()

            for (j in lessonList.indices) {
                if (courseId == lessonList[j].id) {
                    courseId = lessonList[j].courseName
                }
            }

            val date = item.date.split("-")
            if(date.size != 3) {
                break
            }
            val start = startTime.split(":")
            if(start.size != 2) {
                break
            }
            val end = endTime.split(":")
            if(end.size != 2) {
                break
            }

            val bean = DateTime(
                DateTimeBean(date[0].toInt(),date[1].toInt(),date[2].toInt(),start[0].toInt(),start[1].toInt()),
                DateTimeBean(date[0].toInt(),date[1].toInt(),date[2].toInt(),end[0].toInt(),end[1].toInt())
            )
            list.add(
                JxglstuCourseSchedule(
                    time = bean,
                    place = room,
                    courseName = courseId,
                    teacher = item.personName.takeIf { it.isNotBlank() }
                )
            )
        }
    } catch (e : Exception) {
        LogUtil.error(e)
    }
    return list
}

