package com.xah.common.ui.util

import com.xah.common.ui.model.text.PlainText
import com.xah.common.ui.model.text.StringResText

fun res(id: Int, vararg args: Any) = StringResText(id, args.toList())

fun text(value: String) = PlainText(value)
