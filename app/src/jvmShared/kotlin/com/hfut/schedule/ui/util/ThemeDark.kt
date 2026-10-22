package com.hfut.schedule.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.model.choice.ColorMode

/** 当前主题是否为深色（跟随用户设置或系统）。原在 WebViewFactory 中，与平台无关，抽到共享层。 */
@Composable
fun isThemeDark() : Boolean {
    val colorCode = remember { ColorMode.entries }
    val setting by DataStoreManager.colorMode.collectAsState(initial = null)
    if(setting != null) {
        val mode = colorCode.find { it.code == setting } ?: ColorMode.AUTO
        return when(mode) {
            ColorMode.AUTO -> isSystemInDarkTheme()
            ColorMode.DARK -> true
            ColorMode.LIGHT -> false
        }
    } else {
        return isSystemInDarkTheme()
    }
}
