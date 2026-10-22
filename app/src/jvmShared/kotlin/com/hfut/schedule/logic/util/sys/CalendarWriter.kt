package com.hfut.schedule.logic.util.sys

/**
 * 写入系统日历。
 *
 * Android 走 CalendarContract（需要权限与 Activity）；桌面端没有等价的系统日历，
 * 实现为空操作（后续可改为导出 .ics 文件）。
 */
expect suspend fun addToCalendars(
    bean: DateTime,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?,
    remind: Boolean
)

/** 用标准格式化的 YYYY-MM-DD / HH:mm 写入日历 */
expect suspend fun addToCalendars(
    startDate: String,
    startTime: String,
    endDate: String,
    endTime: String,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?
)

/** 旧接口：传 [年,月,日,时,分] 列表 */
expect suspend fun addToCalendars(
    start: List<Int>,
    end: List<Int>,
    place: String?,
    title: String,
    remark: String?,
    activity: Any?,
    remind: Boolean
)
