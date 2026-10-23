package com.hfut.schedule.ui.component.qrcode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Hashtable

/**
 * 二维码（跨平台）。
 *
 * Android 原来用 zxing 生成 [android.graphics.Bitmap]，桌面端没有 Bitmap；
 * 这里改成直接把 zxing 的 BitMatrix 画到 Compose Canvas 上，两端零差异、也不用编解码图片。
 */
@Composable
fun QrCodeImage(
    content: String,
    modifier: Modifier = Modifier.fillMaxWidth().aspectRatio(1f),
    contentColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = Color.Transparent,
) {
    val matrix = remember(content) { encodeQr(content) }
    Canvas(modifier = modifier) {
        drawRect(color = backgroundColor, size = size)
        val m = matrix ?: return@Canvas
        val cell = minOf(size.width / m.width, size.height / m.height)
        val x0 = (size.width - cell * m.width) / 2f
        val y0 = (size.height - cell * m.height) / 2f
        for (y in 0 until m.height) {
            for (x in 0 until m.width) {
                if (m[x, y]) {
                    drawRect(
                        color = contentColor,
                        topLeft = Offset(x0 + x * cell, y0 + y * cell),
                        size = Size(cell, cell)
                    )
                }
            }
        }
    }
}

private fun encodeQr(content: String): com.google.zxing.common.BitMatrix? = runCatching {
    val hints = Hashtable<EncodeHintType, String>()
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
    hints[EncodeHintType.ERROR_CORRECTION] = "H"
    hints[EncodeHintType.MARGIN] = "1"
    QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)
}.getOrNull()
