package com.hfut.schedule.logic.util.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

/**
 * 网络图片加载（带 Cookie）。
 *
 * Android 端用 Glide；桌面端用 OkHttp 拉字节 + Skia 解码。
 * 返回的 [MutableState] 在加载完成后被赋值，未完成时为 null。
 */
@Composable
expect fun rememberImageState(
    url: String,
    cookie: String? = null
): MutableState<ImageBitmap?>
