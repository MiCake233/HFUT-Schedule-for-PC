package com.hfut.schedule.logic.util.other

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap? = runCatching {
    org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
}.getOrNull()
