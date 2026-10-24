package com.hfut.schedule.logic.util.sys

import com.xah.common.ui.res.Drawables
import com.xah.navigation.controller.NavigationController
import java.awt.Desktop
import java.net.URI

/**
 * 桌面端跳转实现：
 * - 所有网页统一交给系统默认浏览器（不内嵌 Chromium，避免 KCEF 的编译与体积代价）
 * - Android 专属的「启动第三方 App / 跳设置页 / 登录成功页」在桌面上降级为提示
 */
actual object PlatformStarter {

    actual fun openExternal(url: String) {
        if (!openInSystemBrowser(url)) showToast("打开浏览器失败：$url")
    }

    actual fun openWebPage(url: String, title: String, cookie: String?, icon: Int?) {
        openInSystemBrowser(url)
    }

    actual fun openInAppWeb(
        navController: NavigationController,
        url: String,
        title: String,
        cookie: String?,
        icon: Int
    ) {
        // 桌面端没有可用的内嵌 WebView 容器，统一用系统浏览器打开
        openInSystemBrowser(url)
    }

    actual fun launchApp(packageName: String, appName: String) {
        showToast("桌面端不支持启动$appName")
    }

    actual fun openAppUrl(url: String, appName: String?) {
        if (!openInSystemBrowser(url)) showToast("打开失败：$url")
    }

    actual fun startPddExpress() = showToast("桌面端不支持拼多多快递查询")

    actual fun startTaoBaoExpress() = showToast("桌面端不支持淘宝快递查询")

    actual fun openWlanSettings() = showToast("请使用 Windows 网络设置")

    actual fun restartToLogin() = showToast("请重新登录")

    actual fun showLoginSuccess() {
        // 桌面端：由窗口层切到主界面；未注册钩子时退化为提示
        val hook = com.hfut.schedule.logic.util.sys.PlatformHooks.onLoginSuccess
        if (hook != null) hook() else showToast("登录成功")
    }

    actual fun showFix() {
        val hook = com.hfut.schedule.logic.util.sys.PlatformHooks.onShowFix
        if (hook != null) hook() else showToast("桌面端暂未注册修复页")
    }

    actual fun sendMail(address: String) {
        if (!openInSystemBrowser("mailto:$address")) {
            ClipBoardHelper.copy(address, "已复制邮箱地址")
            showToast("未找到邮件客户端，请手动发送到 $address")
        }
    }

    private fun openInSystemBrowser(url: String): Boolean = try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
            true
        } else {
            false
        }
    } catch (e: Exception) {
        com.xah.common.logic.util.LogUtil.error(e)
        false
    }
}

/** 桌面端图标资源占位（保持与 Android 相同签名） */
val desktopWebIcon: Int = Drawables.net
