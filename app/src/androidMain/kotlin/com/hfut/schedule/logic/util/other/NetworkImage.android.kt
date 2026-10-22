package com.hfut.schedule.logic.util.other

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
actual fun rememberImageState(
    url: String,
    cookie: String?
): MutableState<ImageBitmap?> {
    val context = LocalContext.current
    val imageState = remember { mutableStateOf<ImageBitmap?>(null) }
    val requestManager = remember(context) {
        Glide.with(context.applicationContext)
    }

    DisposableEffect(url, cookie) {
        val headers = LazyHeaders.Builder().apply {
            cookie?.let { addHeader("Cookie", it) }
        }.build()
        val glideUrl = GlideUrl(url, headers)

        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                imageState.value = resource.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        }

        Glide.with(context)
            .asBitmap()
            .load(glideUrl)
            .into(target)

        onDispose {
            requestManager.clear(target)
        }
    }

    return imageState
}
