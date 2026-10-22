package com.hfut.schedule.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.screen.welcome.UseAgreementScreen
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

/** 桌面窗口级状态：登录态 */
object DesktopWindowState {
    var loggedIn by mutableStateOf(false)
}

/**
 * 桌面端根页面。
 *
 * 三条路径全部复用共享层页面：
 * 1) 未同意协议 → 欢迎页 UseAgreementScreen
 * 2) 未登录     → 登录页 LoginScreen（与 Android 完全同一份实现）
 * 3) 已登录     → 桌面主界面 DesktopHomeScreen
 */
object DesktopRootDestination : NavDestination() {
    override val key: String = "desktop_root"
    override val title: UiText = text("HFUT 课程表")
    override val icon: Int = Drawables.calendar

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val loginVm = LocalNavDependencies.current.get<LoginViewModel>()
        val enableUse by DataStoreManager.enableUse.collectAsState(initial = DataStoreManager.getSyncEnableUse())

        when {
            !enableUse -> UseAgreementScreen()
            !DesktopWindowState.loggedIn -> LoginScreen(loginVm, networkVm)
            else -> DesktopHomeScreen(networkVm)
        }
    }
}
