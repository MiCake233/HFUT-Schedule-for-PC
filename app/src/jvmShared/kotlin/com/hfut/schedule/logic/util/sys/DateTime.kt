package com.hfut.schedule.logic.util.sys

import java.time.YearMonth

/**
 * 日程/课程的时间结构。原定义放在 CalendarUtils.kt（Android 专属文件）里，
 * 由于 Room 实体与跨平台 UI 都要用，抽到共享源集。
 */
data class DateTime(val start : DateTimeBean, val end : DateTimeBean)

data class DateTimeBean(val year : Int,val month : Int,val day : Int,val hour : Int,val minute : Int = 0) {
    init {
        require(year > 1900) { "年份必须大于1900" }
        require(month in 1..12) { "月份必须在1到12之间" }
        require(hour in 0..23) { "小时必须在0到23之间" }
        require(minute in 0..59) { "分钟必须在0到59之间" }
        // 计算该年月的最大天数
        val maxDays = YearMonth.of(year, month).lengthOfMonth()
        require(day in 1..maxDays) { "日期必须在1到${maxDays}之间" }
    }
}
