package com.hfut.schedule.logic.util.sys

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.enumeration.Language
import com.xah.common.logic.util.LogUtil
import com.xah.common.logic.util.isEmptyOrBlank

actual object LanguageHelper {
    private fun currentTag(context: Context): String? = context.resources.configuration.locale.language

    actual fun isChineseLanguage(): Boolean = currentTag(MyApplication.context) == "zh"
    actual fun isEnglishLanguage(): Boolean = currentTag(MyApplication.context) == "en"

    private fun setAppLanguage(languageTag: String) {
        val locales = if (languageTag.isEmptyOrBlank()) LocaleListCompat.getEmptyLocaleList()
        else LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    actual fun changeAppLanguage(code: Int) {
        val language = Language.entries.find { it.code == code } ?: return
        LogUtil.debug("change to ${language.label}")
        when (language) {
            Language.ENGLISH -> setAppLanguage("en")
            Language.AUTO -> setAppLanguage("")
            Language.CHINESE_SIMPLY -> setAppLanguage("zh-CN")
        }
    }
}