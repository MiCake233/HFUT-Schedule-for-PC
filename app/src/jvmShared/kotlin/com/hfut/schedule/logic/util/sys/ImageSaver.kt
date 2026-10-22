package com.hfut.schedule.logic.util.sys

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 保存图片到系统相册/图片目录。
 * Android 写入 Pictures；桌面端写入用户「图片」目录（或应用数据目录）。
 */
expect suspend fun saveImageToGallery(bitmap: ImageBitmap, fileName: String? = null): String?