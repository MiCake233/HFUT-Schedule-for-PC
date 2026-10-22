package com.hfut.schedule.logic.util.sys

import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.xah.common.logic.util.LogUtil
import java.io.File
import java.util.UUID

actual suspend fun saveImageToGallery(bitmap: ImageBitmap, fileName: String?): String? = try {
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "${fileName ?: UUID.randomUUID().toString()}.png")
    file.outputStream().use { out ->
        bitmap.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
    }
    file.absolutePath
} catch (e: Exception) {
    LogUtil.error(e)
    null
}