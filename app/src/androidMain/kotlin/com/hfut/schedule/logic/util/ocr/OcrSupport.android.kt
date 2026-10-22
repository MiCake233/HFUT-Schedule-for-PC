package com.hfut.schedule.logic.util.ocr

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.hfut.schedule.logic.util.ocr.preprocessCaptcha as preprocessBitmap

actual object OcrSupport {
    actual fun isModelReady(): Boolean = runCatching { TesseractUtils.isExistModule() }.getOrDefault(false)

    actual fun recognizeCaptcha(image: ImageBitmap): String? =
        runCatching { TesseractUtils.recognizeCaptcha(image.asAndroidBitmap()) }.getOrNull()

    actual fun preprocessCaptcha(image: ImageBitmap): ImageBitmap =
        runCatching { preprocessCaptcha(image.asAndroidBitmap()) }.getOrDefault(image)
}