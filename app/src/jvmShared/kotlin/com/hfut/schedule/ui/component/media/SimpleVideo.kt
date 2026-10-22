package com.hfut.schedule.ui.component.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 视频播放与视频下载。
 * Android 用系统 MediaPlayer/VideoView + 外部文件目录；
 * 桌面端不做视频播放（属于「PC 端无对等物」的部分），返回空实现。
 */
@Composable
expect fun SimpleVideo(
    filePath: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    mute: Boolean = true,
    loop: Boolean = true,
    aspectRatio: Float? = null
)

/**
 * 检查本地是否已有视频，没有则下载。返回本地路径；桌面端恒为 null。
 * @param context Android 侧用于取外部文件目录，桌面端忽略
 */
expect suspend fun checkOrDownloadVideo(
    context: Any?,
    fileName: String,
    downloadUrl: String
): String?
