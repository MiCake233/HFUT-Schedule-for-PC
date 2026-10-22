package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import com.xah.common.ui.res.Drawables


enum class TimeTableType(
    val description: String,
    val icon : Int
) {
    COURSE("课程", Drawables.calendar),FOCUS("日程", Drawables.lightbulb),EXAM("考试", Drawables.draw)
}