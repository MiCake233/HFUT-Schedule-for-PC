package com.hfut.schedule.logic.util.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.compose.resources.decodeToImageBitmap

private val imageClient by lazy { OkHttpClient() }

/**
 * 桌面端：OkHttp 拉取图片字节，再用 Compose Resources 的解码器转成 ImageBitmap。
 */
@Composable
actual fun rememberImageState(
    url: String,
    cookie: String?
): MutableState<ImageBitmap?> {
    val state = remember(url, cookie) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url, cookie) {
        if (url.isBlank()) return@LaunchedEffect
        val image = withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url(url)
                    .apply { cookie?.let { header("Cookie", it) } }
                    .build()
                imageClient.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) return@runCatching null
                    resp.body?.bytes()?.decodeToImageBitmap()
                }
            }.onFailure { LogUtil.error(it) }.getOrNull()
        }
        state.value = image
    }

    return state
}
