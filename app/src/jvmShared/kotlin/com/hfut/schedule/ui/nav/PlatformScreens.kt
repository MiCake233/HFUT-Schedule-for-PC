package com.hfut.schedule.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.model.text.asStringNow
import com.xah.common.ui.res.painterResource

/**
 * 平台专属页面。
 *
 * 有些页面只在 Android 端存在（内嵌 WebView、相机扫码、控制中心磁贴等），
 * 共享层的 Destination 通过这个入口渲染；桌面端返回 false，由 [UnavailableFeature] 兜底。
 */
@Composable
expect fun RenderPlatformScreen(key: String): Boolean

/**
 * 平台专属页面的**类型分发**入口。
 *
 * 参数直接挂在 Destination 对象上（如 GradeDestination(ifSaved)），
 * Android 端在注册表里按类型渲染真实界面，桌面端返回 false 由 [UnavailableFeature] 兜底。
 */
@Composable
expect fun RenderPlatformDestination(destination: com.hfut.schedule.ui.nav.destination.base.NavDestination): Boolean

/** 桌面端的功能占位：说明该功能仅 Android 提供 */
@Composable
fun UnavailableFeature(title: UiText) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(Drawables.desktop_windows),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title.asStringNow(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "该功能依赖 Android 系统能力，电脑端暂不提供。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
