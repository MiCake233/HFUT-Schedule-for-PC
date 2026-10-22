package com.hfut.schedule.ui.component.button

import androidx.compose.runtime.Composable

/**
 * 平台无关的「液态玻璃背景源」。
 *
 * Android 端直接复用 kyant backdrop 库的 Backdrop（actual typealias），
 * 桌面端用自研的轻量实现（见 jvmMain），保证 UI 层 API 完全一致。
 */
expect interface PlatformBackdrop

/**
 * 记录当前图层作为背景源。
 * 名字与原 kyant API 保持一致，方便原代码零改动迁移。
 */
@Composable
expect fun rememberLayerBackdrop(): PlatformBackdrop
