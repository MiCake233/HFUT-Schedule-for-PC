package com.xah.common.ui.model.text

import androidx.compose.runtime.Composable
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.stringResource

/**
 * 字符串资源文本。[resId] 是 [Strings] 注册表里的稳定索引（由 Android R.string 迁移而来）。
 */
data class StringResText(
    val resId: Int,
    val args: List<Any> = emptyList()
) : UiText {
    @Composable
    override fun asString(): String = stringResource(resId, *args.toTypedArray())

    override suspend fun asStringSuspend(): String =
        org.jetbrains.compose.resources.getString(Strings.resourceOf(resId), *args.toTypedArray())
}
