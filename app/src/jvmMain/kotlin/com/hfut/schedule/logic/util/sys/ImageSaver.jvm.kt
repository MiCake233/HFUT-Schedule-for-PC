package com.hfut.schedule.logic.util.sys

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import com.xah.common.logic.util.LogUtil
import com.xah.common.platform.Platform
import java.io.File
import javax.imageio.ImageIO
import java.util.UUID

actual suspend fun saveImageToGallery(bitmap: ImageBitmap, fileName: String?): String? = try {
    val dir = File(System.getProperty("user.home"), "Pictures").let {
        if (it.exists() || it.mkdirs()) it else File(Platform.dataDirPath(), "pictures").apply { mkdirs() }
    }
    val file = File(dir, "${fileName ?: UUID.randomUUID().toString()}.png")
    ImageIO.write(bitmap.toAwtImage(), "png", file)
    file.absolutePath
} catch (e: Exception) {
    LogUtil.error(e)
    null
}