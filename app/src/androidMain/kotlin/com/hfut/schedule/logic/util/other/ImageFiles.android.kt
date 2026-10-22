package com.hfut.schedule.logic.util.other

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.hfut.schedule.ui.util.loadBitmap
import com.hfut.schedule.ui.util.pickColorFromTop
import java.io.File

actual suspend fun loadImageBitmap(file: File): ImageBitmap? =
    loadBitmap(file)?.asImageBitmap()

actual fun pickColorFromTop(image: ImageBitmap): Int? =
    pickColorFromTop(image.asAndroidBitmap())
