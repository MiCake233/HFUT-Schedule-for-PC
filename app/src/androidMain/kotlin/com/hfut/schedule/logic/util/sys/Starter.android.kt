package com.hfut.schedule.logic.util.sys

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.service.quicksettings.TileService
import androidx.core.net.toUri
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.activity.screen.FixActivity
import com.hfut.schedule.activity.screen.SuccessActivity
import com.hfut.schedule.activity.util.WebViewActivity
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.enumeration.BottomBarItems
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.util.WebVpnConvertor
import com.hfut.schedule.ui.nav.destination.HomeDestination
import com.hfut.schedule.ui.nav.destination.WebViewDestination
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.ui.util.getPureUrl
import com.hjq.device.compat.DeviceOs
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.res.Drawables
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.action.LaunchMode

actual object PlatformStarter {

    private fun ctx(): Context = MyApplication.context

    private fun Context.startSafely(intent: Intent): Boolean = try {
        if (this !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        true
    } catch (e: Exception) {
        LogUtil.error(e)
        false
    }

    actual fun openExternal(url: String) {
        if (!ctx().startSafely(Intent(Intent.ACTION_VIEW, url.toUri()))) showToast("启动浏览器失败")
    }

    actual fun openWebPage(url: String, title: String, cookie: String?, icon: Int?) {
        val realUrl: String
        var cookies = cookie
        if (GlobalUiStateHolder.globalWebVpn) {
            val cookieWebVpn = runCatching { getWebVpnCookieBlocking() }.getOrNull()
            cookies = cookieWebVpn
            realUrl = if (url.contains(Constant.WEBVPN_URL)) url else WebVpnConvertor.getWebVpnUrl(url)
        } else {
            realUrl = url
        }
        val intent = Intent(ctx(), WebViewActivity::class.java).apply {
            putExtra("url", realUrl)
            putExtra("title", title)
            cookies?.let { putExtra("cookies", it) }
            icon?.let { putExtra("icon", it) }
        }
        if (!ctx().startSafely(intent)) showToast("启动网页失败")
    }

    actual fun openInAppWeb(
        navController: NavigationController,
        url: String,
        title: String,
        cookie: String?,
        icon: Int
    ) {
        val realUrl: String
        var cookies = cookie
        if (GlobalUiStateHolder.globalWebVpn) {
            val cookieWebVpn = runCatching { getWebVpnCookieBlocking() }.getOrNull()
            if (url.contains(Constant.WEBVPN_URL)) {
                navController.push(WebViewDestination(url = url, name = title, cookies = cookieWebVpn, icon = icon))
            } else {
                navController.push(
                    WebViewDestination(
                        url = WebVpnConvertor.getWebVpnUrl(url),
                        name = title,
                        cookies = cookieWebVpn,
                        icon = icon
                    )
                )
            }
            return
        }
        realUrl = url
        navController.push(WebViewDestination(url = realUrl, name = title, cookies = cookies, icon = icon))
    }

    /** getWebVpnCookie 是 suspend，这里同步桥接（内部只是一次本地读） */
    private fun getWebVpnCookieBlocking(): String? =
        kotlinx.coroutines.runBlocking { getWebVpnCookie() }

    actual fun launchApp(packageName: String, appName: String) {
        try {
            val intent = ctx().packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) showToast("未安装$appName") else ctx().startActivity(intent)
        } catch (e: Exception) {
            LogUtil.error(e)
            if (DeviceOs.isHarmonyOsNextAndroidCompatible()) {
                showToast("检测到为鸿蒙NEXT，启动外部应用失败")
            } else {
                showToast("启动外部应用失败")
            }
        }
    }

    actual fun openAppUrl(url: String, appName: String?) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, url.toUri())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx().startActivity(intent)
        } catch (e: Exception) {
            LogUtil.error(e)
            val name = appName ?: url.substringBefore("://")
            showToast("打开${name}失败")
        }
    }

    actual fun startPddExpress() {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    Starter.AppPackages.PDD.packageName,
                    "${Starter.AppPackages.PDD.packageName}.ui.activity.MainFrameActivity"
                )
                data = Constant.PDD_PACKAGE_URL.toUri()
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx().startActivity(intent)
        } catch (e: Exception) {
            LogUtil.error(e)
            showToast("打开${Starter.AppPackages.PDD.appName}失败")
        }
    }

    actual fun startTaoBaoExpress() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Constant.TAO_BAO_PACKAGE_ID_URL.toUri()
                setPackage(Starter.AppPackages.TAO_BAO.packageName)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx().startActivity(intent)
        } catch (e: Exception) {
            LogUtil.error(e)
            showToast("打开${Starter.AppPackages.TAO_BAO.appName}失败")
        }
    }

    actual fun openWlanSettings() {
        if (!ctx().startSafely(Intent(Settings.ACTION_WIFI_SETTINGS))) showToast("打开WLAN设置失败")
    }

    actual fun restartToLogin() {
        ctx().startSafely(Intent(ctx(), MainActivity::class.java).apply { putExtra("login", true) })
    }

    actual fun showLoginSuccess() {
        ctx().startSafely(Intent(ctx(), SuccessActivity::class.java))
    }

    actual fun showFix() {
        ctx().startSafely(Intent(ctx(), FixActivity::class.java))
    }

    actual fun sendMail(address: String) {
        val ok = ctx().startSafely(Intent(Intent.ACTION_SENDTO, "mailto:$address".toUri()))
        if (!ok) {
            ClipBoardHelper.copy(address, "已复制邮箱地址")
            showToast("启动邮箱应用失败，请向${address}发送邮件")
        }
    }
}

// ---------- Android 专属的旧入口（桌面端没有等价物，保留在 androidMain） ----------

fun NavigationController.backToHome(
    subStartDestination: BottomBarItems = BottomBarItems.FOCUS,
) {
    push(
        HomeDestination(subStartDestination),
        launchMode = LaunchMode.Clear(),
    )
}

fun openDownloadFolder(activity: Activity) {
    try {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val uri = Uri.fromFile(downloads)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }
        activity.startActivityForResult(intent, 100)
    } catch (e: Exception) {
        LogUtil.error(e)
        showToast("启动下载管理失败")
    }
}

fun TileService.startActivitySafely(intent: Intent) {
    if (AppVersion.sdkInt >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        startActivityAndCollapse(pendingIntent)
    } else {
        @Suppress("DEPRECATION")
        startActivityAndCollapse(intent)
    }
}

/** 兼容旧签名：仍需要 Context 的内嵌网页打开 */
fun openWebInner(url: String, title: String = getPureUrl(url), cookie: String? = null, icon: Int = Drawables.net) {
    PlatformStarter.openWebPage(url, title, cookie, icon)
}
