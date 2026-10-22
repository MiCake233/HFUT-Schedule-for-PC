package com.hfut.schedule.logic.util.ocr

import androidx.compose.ui.graphics.ImageBitmap

actual object OcrSupport {
    actual fun isModelReady(): Boolean = false
    actual fun recognizeCaptcha(image: ImageBitmap): String? = null
    actual fun preprocessCaptcha(image: ImageBitmap): ImageBitmap = image
}