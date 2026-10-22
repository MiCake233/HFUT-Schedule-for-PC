package com.hfut.schedule.logic.util.sys

import com.xah.common.ui.res.Drawables
import com.xah.navigation.controller.NavigationController

/**
 * 统一的外部跳转入口。
 *
 * 原来所有方法都要传 `android.content.Context`，桌面端没有 Context；
 * 这里改成「共享门面 + 平台实现」：门面签名不带 Context，
 * 具体打开浏览器 / 内嵌页 / 启动 App 由 [PlatformStarter] 的平台实现完成。
 */
object Starter {

    enum class AppPackages(
        val packageName: String,
        val appName: String,
        val icon: Int,
    ) {
        TODAY_CAMPUS("com.wisedu.cpdaily", "今日校园", Drawables.today_campus_icon),
        WECHAT("com.tencent.mm", "微信", Drawables.wechat_icon),
        CHAO_XING("com.chaoxing.mobile", "学习通", Drawables.chao_xing_icon),
        MOOC("com.netease.edu.ucmooc", "中国大学MOOC", Drawables.mooc_icon),
        RAIN_CLASSROOM("com.xuetangx.ykt", "雨课堂", Drawables.rain_classroom_icon),
        LE_PAO("com.yunzhi.tiyu", "云运动", Drawables.le_pao_icon),
        ANHUI_HALL("com.iflytek.oshall.ahzwfw", "皖事通", Drawables.anhui_hall_icon),
        ALIPAY("com.eg.android.AlipayGphone", "支付宝", Drawables.alipay_icon),
        PDD("com.xunmeng.pinduoduo", "拼多多", Drawables.pdd_icon),
        TAO_BAO("com.taobao.taobao", "淘宝", Drawables.taobao_icon),
    }

    // 通过包名启动第三方应用
    fun startAppLaunch(app: AppPackages) = PlatformStarter.launchApp(app.packageName, app.appName)

    fun startWlanSettings() = PlatformStarter.openWlanSettings()

    fun startPddExpress() = PlatformStarter.startPddExpress()

    fun startTaoBaoExpress() = PlatformStarter.startTaoBaoExpress()

    fun startAppUrl(url: String, appName: String? = null) = PlatformStarter.openAppUrl(url, appName)

    /** 用外部浏览器打开 */
    fun startWebUrlOuter(url: String) = PlatformStarter.openExternal(url)

    /** 重新进入登录页（Android 重启 Activity；桌面重置为登录态） */
    fun refreshLogin() = PlatformStarter.restartToLogin()

    /** 用软件内网页容器打开（Android: WebViewActivity；桌面: 系统浏览器） */
    suspend fun startWebUrlInner(
        url: String,
        title: String = getPureUrl(url),
        cookie: String? = null,
        icon: Int? = null
    ) = PlatformStarter.openWebPage(url, title, cookie, icon)

    /** 用应用内导航栈打开网页（Android: WebViewDestination；桌面: 系统浏览器） */
    suspend fun startWebUrlInner(
        navController: NavigationController,
        url: String,
        title: String = getPureUrl(url),
        cookie: String? = null,
        icon: Int = Drawables.net,
    ) = PlatformStarter.openInAppWeb(navController, url, title, cookie, icon)

    fun emailMe() = PlatformStarter.sendMail("zsh0908@outlook.com")

    fun loginSuccess() = PlatformStarter.showLoginSuccess()

    fun startFix() = PlatformStarter.showFix()
}

/**
 * 各平台的跳转能力。
 */
expect object PlatformStarter {
    fun openExternal(url: String)
    fun openWebPage(url: String, title: String, cookie: String?, icon: Int?)
    fun openInAppWeb(navController: NavigationController, url: String, title: String, cookie: String?, icon: Int)
    fun launchApp(packageName: String, appName: String)
    fun openAppUrl(url: String, appName: String?)
    fun startPddExpress()
    fun startTaoBaoExpress()
    fun openWlanSettings()
    fun restartToLogin()
    fun showLoginSuccess()
    fun showFix()
    fun sendMail(address: String)
}
