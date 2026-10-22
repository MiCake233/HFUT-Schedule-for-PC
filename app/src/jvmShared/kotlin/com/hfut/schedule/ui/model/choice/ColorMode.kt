package com.hfut.schedule.ui.model.choice

import com.xah.common.ui.res.Strings

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

enum class ColorMode(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    LIGHT(res(Strings.appearance_settings_choice_theme_light),1),
    DARK(res(Strings.appearance_settings_choice_theme_dark),2),
    AUTO(res(Strings.appearance_settings_choice_theme_auto),0)
}