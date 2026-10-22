package com.hfut.schedule.logic.util.ocr

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 验证码识别（离线 Tesseract）。
 * Android 端有可下载的 11MB 模型；桌面端暂不支持，全部降级。
 */
expect object OcrSupport {
    fun isModelReady(): Boolean
    fun recognizeCaptcha(image: ImageBitmap): String?
    fun preprocessCaptcha(image: ImageBitmap): ImageBitmap
}