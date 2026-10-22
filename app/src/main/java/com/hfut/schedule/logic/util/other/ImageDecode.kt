package com.hfut.schedule.logic.util.other

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * 把图片字节解码为 Compose ImageBitmap。
 * 数据层只传递 ByteArray（不再持有 Android Bitmap），解码下沉到 UI 层，
 * 桌面端在 jvmMain 里有一份基于 Skia 的实现。
 */
fun decodeImageBitmap(bytes: ByteArray): ImageBitmap? =
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
