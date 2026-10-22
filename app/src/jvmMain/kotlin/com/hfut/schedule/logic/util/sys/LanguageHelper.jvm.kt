package com.hfut.schedule.logic.util.sys

/** 桌面端固定使用简体中文 */
actual object LanguageHelper {
    actual fun isChineseLanguage(): Boolean = true
    actual fun isEnglishLanguage(): Boolean = false
    actual fun changeAppLanguage(code: Int) = Unit
}