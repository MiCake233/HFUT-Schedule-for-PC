package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.nav.RenderPlatformScreen
import com.hfut.schedule.ui.nav.UnavailableFeature
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.util.text

/**
 * 其余「只在 Android 端实现」的设置子页。
 *
 * 共享页面（网络设置、实验室、慧新登录等）需要跳到这些页，
 * 这里统一用 key 走平台注册表：Android 端渲染真实界面，桌面端显示占位。
 */
object SettingsAiApiKeyPlatformDestination : NavDestination() {
    override val key: String = "platform_settings_ai_api_key"
    override val title: UiText = text("AI 接口密钥")
    override val icon: Int = Drawables.key

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}

object SettingsHuiXinPasswordPlatformDestination : NavDestination() {
    override val key: String = "platform_settings_huixin_password"
    override val title: UiText = text("慧新易校密码")
    override val icon: Int = Drawables.lock

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}

object SettingsBackupPlatformDestination : NavDestination() {
    override val key: String = "platform_settings_backup"
    override val title: UiText = text("备份与恢复")
    override val icon: Int = Drawables.save

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}

object SettingsTipsPlatformDestination : NavDestination() {
    override val key: String = "platform_settings_tips"
    override val title: UiText = text("使用提示")
    override val icon: Int = Drawables.lightbulb

    @Composable
    override fun Content() {
        if (!RenderPlatformScreen(key)) UnavailableFeature(title)
    }
}
