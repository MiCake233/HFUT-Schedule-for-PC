package com.hfut.schedule.ui.screen.home.calendar.multi

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Strings
import com.xah.common.ui.util.res

/**
 * 课表数据源类型。原先定义在 MultiScheduleSettings.kt（Android 专属文件）里，
 * 但 DataStoreManager 等共享代码都要用，抽到共享源集。
 */
enum class CourseType(override val code: Int, override val label: UiText) : BaseChoice {
    JXGLSTU(0, res(Strings.app_settings_default_calendar_choice_jxglstu)),
    COMMUNITY(1, res(Strings.app_settings_default_calendar_choice_community)),
    UNI_APP(2, res(Strings.app_settings_default_calendar_choice_uni_app)),
    ZHI_JIAN(3, res(Strings.app_settings_default_calendar_choice_zhi_jian)),
    JXGLSTU2(4, res(Strings.app_settings_default_calendar_choice_jxglstu_2)),
}
