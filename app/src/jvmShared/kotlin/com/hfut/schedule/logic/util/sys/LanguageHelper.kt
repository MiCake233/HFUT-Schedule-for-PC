package com.hfut.schedule.logic.util.sys

/** 应用语言（简体中文/英文/跟随系统） */
expect object LanguageHelper {
    fun isChineseLanguage(): Boolean
    fun isEnglishLanguage(): Boolean
    fun changeAppLanguage(code: Int)
}