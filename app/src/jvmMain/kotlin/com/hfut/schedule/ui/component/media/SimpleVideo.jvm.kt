package com.hfut.schedule.ui.component.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 桌面端不播放视频，只占位 */
@Composable
actual fun SimpleVideo(
    filePath: String,
    modifier: Modifier,
    autoPlay: Boolean,
    mute: Boolean,
    loop: Boolean,
    aspectRatio: Float?
) {
    Box(
        modifier = modifier.fillMaxWidth().height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "桌面端暂不支持视频播放",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

actual suspend fun checkOrDownloadVideo(
    context: Any?,
    fileName: String,
    downloadUrl: String
): String? = null
