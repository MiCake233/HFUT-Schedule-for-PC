package com.xah.common.ui.model.text

import androidx.compose.runtime.Composable

/**
 * 可组合环境里用 [asString]；非组合环境（如 Android Shortcut 构建）用 [asStringSuspend]。
 * 跨平台后不再持有 android.content.Context。
 */
sealed interface UiText {
    @Composable
    fun asString(): String

    suspend fun asStringSuspend(): String
}
