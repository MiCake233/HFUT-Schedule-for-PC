package com.hfut.schedule.logic.util.other

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeImageBitmap(bytes: ByteArray): ImageBitmap?

/** Base64 文本解码为字节数组（兼容换行与无关字符）。 */
fun decodeImageBase64(text: String): ByteArray =
    runCatching { java.util.Base64.getMimeDecoder().decode(text) }.getOrElse { ByteArray(0) }
