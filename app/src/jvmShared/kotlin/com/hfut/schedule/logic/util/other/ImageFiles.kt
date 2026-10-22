package com.hfut.schedule.logic.util.other

import androidx.compose.ui.graphics.ImageBitmap
import java.io.File

/**
 * 读取本地图片（自定义背景/壁纸）。
 * Android 用 BitmapFactory；桌面端用 Skia 解码。
 */
expect suspend fun loadImageBitmap(file: File): ImageBitmap?

/** 取图片顶部一行的主色，用于顶栏/状态栏染色；取不到返回 null */
expect fun pickColorFromTop(image: ImageBitmap): Int?
