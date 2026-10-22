package com.hfut.schedule.logic.util.sys

/**
 * 桌面端没有系统日历，写入日程为空操作。
 * TODO: 后续可改为导出 .ics 文件，用系统日历应用导入。
 */
actual suspend fun addToCalendars(
    bean: DateTime,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?,
    remind: Boolean
) = Unit

actual suspend fun addToCalendars(
    startDate: String,
    startTime: String,
    endDate: String,
    endTime: String,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?
) = Unit

actual suspend fun addToCalendars(
    start: List<Int>,
    end: List<Int>,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?,
    remind: Boolean
) = Unit
