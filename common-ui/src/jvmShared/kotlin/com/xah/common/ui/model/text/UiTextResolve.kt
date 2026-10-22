package com.xah.common.ui.model.text

import com.xah.common.ui.res.stringOf

/**
 * 非组合、非挂起环境下的取文案（原代码里大量 `title.asString(context)`）。
 * 内部按需阻塞读一次资源并缓存，之后为纯内存查表。
 */
fun UiText.asStringNow(): String = when (this) {
    is PlainText -> value
    is StringResText -> if (args.isEmpty()) stringOf(resId) else stringOf(resId, *args.toTypedArray())
}
