package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.SwingUtilities



/** 桌面端导出：把报告内容离屏渲染为 PNG，保存到下载目录。 */
actual suspend fun exportTermReportImage(
    vm: NetWorkViewModel,
    semester: Int,
    modules: Set<TermReportExportModule>,
    isGraduating: Boolean,
    allSemesters: List<Int>
): String? = withContext(Dispatchers.IO) {
    if (modules.isEmpty()) return@withContext null
    val widthPx = 1080
    val heightPx = 7200

    var image: java.awt.image.BufferedImage? = null
    SwingUtilities.invokeAndWait {
        runCatching {
            val scene = ImageComposeScene(
                width = widthPx,
                height = heightPx,
                density = Density(1f)
            ) {
                MaterialTheme {
                    Box(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                        TermReportExportContent(
                            vm = vm,
                            semester = semester,
                            modules = modules,
                            isGraduating = isGraduating,
                            allSemesters = allSemesters
                        )
                    }
                }
            }
            try {
                // 等待异步数据（网络 / 缓存）就绪，多渲染几次
                repeat(30) {
                    scene.render(0)
                    Thread.sleep(200)
                }
                val skiaImage = scene.render(0)
                val pngBytes = skiaImage.encodeToData(org.jetbrains.skia.EncodedImageFormat.PNG)?.bytes
                if (pngBytes != null) {
                    image = ImageIO.read(pngBytes.inputStream())
                }
            } finally {
                scene.close()
            }
        }
    }

    val rendered = image ?: return@withContext null
    val trimmed = trimBottomBlanks(rendered)
    val dir = File(System.getProperty("user.home"), "Downloads").let {
        if (it.isDirectory) it else File(System.getProperty("user.home"))
    }
    val file = File(dir, "学期报告_${System.currentTimeMillis()}.png")
    runCatching { ImageIO.write(trimmed, "png", file) }
    file.absolutePath
}

/** 去掉底部空白，避免固定高度造成大片留白。 */
private fun trimBottomBlanks(source: BufferedImage): BufferedImage {
    val background = source.getRGB(source.width / 2, source.height - 2)
    var lastRow = source.height - 1
    while (lastRow > 0) {
        var allSame = true
        var x = 0
        while (x < source.width) {
            if (source.getRGB(x, lastRow) != background) { allSame = false; break }
            x += 8
        }
        if (!allSame) break
        lastRow--
    }
    val height = (lastRow + 40).coerceIn(1, source.height)
    return source.getSubimage(0, 0, source.width, height)
}