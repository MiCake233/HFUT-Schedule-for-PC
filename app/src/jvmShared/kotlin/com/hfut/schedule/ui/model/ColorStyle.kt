package com.hfut.schedule.ui.model

import com.xah.common.ui.res.Strings

import com.hfut.schedule.R
import com.materialkolor.PaletteStyle
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

enum class ColorStyle(val code : Int, val description: UiText, val style : PaletteStyle) {
    DEFAULT(2, res(Strings.appearance_settings_choice_bright_normal), PaletteStyle.TonalSpot),
    LIGHT(1, res(Strings.appearance_settings_choice_bright_tinge), PaletteStyle.Neutral),
    DEEP(3, res(Strings.appearance_settings_choice_bright_heavy), PaletteStyle.Vibrant),
    BLACK(0, res(Strings.appearance_settings_choice_bright_monochrome), PaletteStyle.Monochrome),
}