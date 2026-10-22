package com.hfut.schedule.logic.util.sys
import java.time.ZoneOffset

import java.time.LocalDateTime

/** UTC 与北京时间的换算（原在 CalendarUtils 中，与平台无关） */
fun DateTimeBean.toUTC(offsetHours: Int = 8): DateTimeBean {
    // 构造本地时区的 LocalDateTime
    val localDateTime = LocalDateTime.of(year, month, day, hour, minute)

    // 假设当前时间是东X区（默认东八区）
    val zoneOffset = ZoneOffset.ofHours(offsetHours)
    val zonedDateTime = localDateTime.atOffset(zoneOffset)

    // 转换为 UTC
    val utcDateTime = zonedDateTime.withOffsetSameInstant(ZoneOffset.UTC)

    // 构造并返回转换后的 DateTimeBean
    return DateTimeBean(
        year = utcDateTime.year,
        month = utcDateTime.monthValue,
        day = utcDateTime.dayOfMonth,
        hour = utcDateTime.hour,
        minute = utcDateTime.minute
    )
}

fun DateTimeBean.fromUTC(offsetHours: Int = 8): DateTimeBean = toUTC(-offsetHours)
