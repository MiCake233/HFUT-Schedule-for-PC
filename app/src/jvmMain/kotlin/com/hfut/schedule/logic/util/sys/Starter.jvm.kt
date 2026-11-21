package com.hfut.schedule.logic.util.sys
import com.xah.common.ui.res.Drawables
import com.xah.navigation.controller.NavigationController

/**
 * 桌面端跳转实现：
 * - 普通网页交给系统默认浏览器（不内嵌 Chromium，避免 KCEF 的编译与体积代价）
 * - 需要 Cookie 或地址里带凭据的页面不打开，也不把地址放进提示
 * - Android 专属的「启动第三方 App / 跳设置页」在桌面上降级为提示
 */
actual object PlatformStarter {

    actual fun openExternal(url: String) {
        openDesktopWeb(url)
    }

    actual fun openWebPage(url: String, title: String, cookie: String?, icon: Int?) {
        openDesktopWeb(url, cookie)
    }

    actual fun openInAppWeb(
        navController: NavigationController,
        url: String,
        title: String,
        cookie: String?,
        icon: Int
    ) {
        openDesktopWeb(url, cookie)
    }

    actual fun launchApp(packageName: String, appName: String) {
        showToast("$appName${com.hfut.schedule.logic.platform.PlatformFeatures.DESKTOP_UNSUPPORTED}")
    }

    actual fun openAppUrl(url: String, appName: String?) {
        openDesktopWeb(url)
    }

    actual fun startPddExpress() = showToast("拼多多快递${com.hfut.schedule.logic.platform.PlatformFeatures.DESKTOP_UNSUPPORTED}")

    actual fun startTaoBaoExpress() = showToast("淘宝快递${com.hfut.schedule.logic.platform.PlatformFeatures.DESKTOP_UNSUPPORTED}")

    actual fun openWlanSettings() {
        val os = System.getProperty("os.name", "").lowercase()
        val hint = if (os.contains("mac")) "请使用系统设置中的无线局域网" else "请使用系统网络设置"
        showToast(hint)
    }

    actual fun restartToLogin() {
        val hook = com.hfut.schedule.logic.util.sys.PlatformHooks.onRestartToLogin
        if (hook != null) hook() else showToast("请重新登录")
    }

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
        if (!openDesktopWeb("mailto:$address", announceFailure = false)) {
            ClipBoardHelper.copy(address, "已复制邮箱地址")
            showToast("未找到邮件客户端，请手动发送到 $address")
        }
    }
}

/** 桌面端图标资源占位（保持与 Android 相同签名） */
val desktopWebIcon: Int = Drawables.net
