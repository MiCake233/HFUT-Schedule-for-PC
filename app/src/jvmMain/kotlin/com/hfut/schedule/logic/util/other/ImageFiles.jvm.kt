package com.hfut.schedule.logic.util.other

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun loadImageBitmap(file: File): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching {
        if (!file.exists()) return@runCatching null
        org.jetbrains.skia.Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
    }.getOrNull()
}

actual fun pickColorFromTop(image: ImageBitmap): Int? {
    if (image.width == 0 || image.height == 0) return null
    val buffer = IntArray(image.width)
    val got = runCatching {
        image.readPixels(buffer = buffer, startX = 0, startY = 0, width = image.width, height = 1)
    }.isSuccess
    if (!got) return null
    return buffer.toList().groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
}
