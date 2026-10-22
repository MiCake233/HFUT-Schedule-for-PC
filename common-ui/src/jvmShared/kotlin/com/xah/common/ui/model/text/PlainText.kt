package com.xah.common.ui.model.text

import androidx.compose.runtime.Composable

data class PlainText(
    val value: String
) : UiText {
    @Composable
    override fun asString(): String = value

    override suspend fun asStringSuspend(): String = value
}
