package com.hfut.schedule.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.nav.destination.HomeDestination
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
 * 显示顺序：用户协议 → 会话恢复（本地存有 TGC 时）→ 登录页或主界面。
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
        var restoring by remember { mutableStateOf(false) }

        LaunchedEffect(enableUse) {
            if (!enableUse) return@LaunchedEffect
            if (DesktopWindowState.loggedIn) return@LaunchedEffect
            if (!DesktopSession.hasSavedSession()) return@LaunchedEffect
            restoring = true
            val redirect = DesktopSession.restore()
            restoring = false
            if (redirect != null) {
                DesktopWindowState.loggedIn = true
                networkVm.jxglstuLogin(redirect)
            }
        }

        when {
            !enableUse -> UseAgreementScreen()
            DesktopWindowState.loggedIn -> HomeDestination().Content()
            restoring -> SessionRestoreScreen()
            else -> LoginScreen(loginVm, networkVm)
        }
    }
}

@Composable
private fun SessionRestoreScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
            Text(
                text = "正在恢复登录状态…",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "使用上次登录保存的凭据",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
