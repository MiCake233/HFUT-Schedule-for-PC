package com.hfut.schedule.ui.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** 桌面端的液态玻璃直接采样窗口背景，不需要记录图层，因此这里是空实现 */
@Composable
actual fun Modifier.layerBackdropSource(backdrop: PlatformBackdrop): Modifier = this
