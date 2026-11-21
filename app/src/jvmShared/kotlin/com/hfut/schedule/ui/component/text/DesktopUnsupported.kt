package com.hfut.schedule.ui.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.platform.PlatformFeatures

/** 桌面端仍保留的 Android 专属操作上的标注。Android 端不应调用。 */
@Composable
fun DesktopUnsupportedLabel(modifier: Modifier = Modifier) {
    Text(
        text = PlatformFeatures.DESKTOP_UNSUPPORTED,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
