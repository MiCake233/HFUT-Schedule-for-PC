package com.hfut.schedule.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.PlatformHooks
import com.hfut.schedule.ui.model.ColorStyle
import com.hfut.schedule.ui.model.choice.ColorMode
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.theme.Typography
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.materialkolor.rememberDynamicColorScheme
import kotlinx.coroutines.launch

/**
 * 桌面端主题。
 *
 * 与 Android 的 AppTheme 同一套逻辑（深浅色 / 自定义取色 / 纯黑 / 配色风格），
 * 只是去掉了 Android 专有的 dynamicColorScheme(context) 与 UiModeManager。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DesktopAppTheme(content: @Composable () -> Unit) {
    val colorMode by DataStoreManager.colorMode.collectAsState(initial = ColorMode.AUTO.code)
    val customColor by DataStoreManager.customColor.collectAsState(initial = -1L)
    val usePureBlack by DataStoreManager.enablePureDark.collectAsState(initial = false)
    val styleCode by DataStoreManager.customColorStyle.collectAsState(initial = ColorStyle.DEFAULT.code)

    val isDark = when (colorMode) {
        ColorMode.DARK.code -> true
        ColorMode.LIGHT.code -> false
        else -> isSystemInDarkTheme()
    }
    // 未自定义时用「工大红」当种子色（桌面端没有系统级动态取色）
    val seed = if (customColor == -1L) Color(0xFFB3121F) else Color(customColor)
    val scheme = rememberDynamicColorScheme(
        seedColor = seed,
        isDark = isDark,
        isAmoled = usePureBlack,
        style = ColorStyle.entries.find { it.code == styleCode }?.style ?: ColorStyle.DEFAULT.style
    )

    MaterialExpressiveTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}

/**
 * 桌面端根容器：建立两个全局 ViewModel，挂上窗口级钩子，然后把渲染交给共享的 [MainHost]。
 *
 * [MainHost] 内部就是 SharedNav 的导航栈 + 转场 + 主题动画，Android 与桌面共用一份实现，
 * 区别只在起始 Destination 与平台能力。
 */
@Composable
fun DesktopApp(exitApp: () -> Unit) {
    val owner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    val networkVm = remember { NetWorkViewModel() }
    val loginVm = remember { LoginViewModel() }
    val scope = rememberCoroutineScope()
    val loggedIn = DesktopWindowState.loggedIn

    // 窗口级钩子：登录成功 / 同意协议 / 退出应用
    DisposableEffect(Unit) {
        PlatformHooks.onLoginSuccess = {
            // 1) 切到「已登录」态：MainHost 会按 Android 的 SuccessActivity 参数重建
            //    （login=false / isSuccessActivity=true / 起始页 = HomeDestination），
            //    这样 MainScreen 以 isLogin=true 进入，课表页签才会执行
            //    「登录各平台 + 拉取课表/一卡通数据」的引导流程。
            DesktopWindowState.loggedIn = true
            // 2) 对齐 Android SuccessActivity.onCreate：用 redirect cookie 换取教务会话
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    if (!com.hfut.schedule.ui.util.state.GlobalUiStateHolder.webVpn) {
                        val redirect = com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
                            .getString("redirect", "") ?: ""
                        if (redirect.isNotEmpty()) {
                            networkVm.jxglstuLogin(redirect)
                        }
                    }
                }.onFailure { com.xah.common.logic.util.LogUtil.error(it) }
            }
        }
        PlatformHooks.onNavigateHome = { /* 协议页同意后 DataStore 会自动置位，无需额外状态 */ }
        PlatformHooks.onExitApp = exitApp
        // 桌面端「选项」页签：接管共享主界面里的 home_settings 槽位
        com.hfut.schedule.ui.nav.UiSlots.register("home_settings") { arg ->
            val args = arg as List<*>
            DesktopSettingsScreen(args[0] as androidx.compose.foundation.layout.PaddingValues)
        }
        // 平台页面工厂：外观设置页在桌面端用自己的实现（Android 那套是壁纸/小组件相关）
        PlatformHooks.platformScreenFactory = { key ->
            if (key == "platform_settings_appearance") {
                val content: @Composable () -> Unit = {
                    val nav = com.xah.navigation.util.LocalNavController.current
                    DesktopAppearanceScreen(onBack = { nav.pop() })
                }
                content
            } else {
                null
            }
        }
        onDispose {
            PlatformHooks.onLoginSuccess = null
            PlatformHooks.onNavigateHome = null
            PlatformHooks.onExitApp = null
            PlatformHooks.platformScreenFactory = null
        }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        DesktopAppTheme {
            // 启动自检标记：UI 真正组合成功后落一个文件，供打包后的自动化验收使用
            androidx.compose.runtime.LaunchedEffect(Unit) {
                runCatching {
                    java.io.File(System.getProperty("java.io.tmpdir"), "hfut-desktop-started.txt")
                        .writeText("UI_STARTED " + java.time.LocalDateTime.now())
                }
            }
            // 登录成功前后是两套 MainHost 参数（对应 Android 的 MainActivity / SuccessActivity），
            // 用 key 强制重建导航栈，否则 rememberNavController 会沿用旧的起始页。
            androidx.compose.runtime.key(loggedIn) {
                MainHost(
                    networkVm = networkVm,
                    loginVm = loginVm,
                    login = !loggedIn,
                    // 与 Android SuccessActivity 一致：登录成功后 isSuccessActivity=true
                    isSuccessActivity = loggedIn,
                    startDestination = if (loggedIn) {
                        com.hfut.schedule.ui.nav.destination.HomeDestination()
                    } else {
                        DesktopRootDestination
                    },
                )
            }
        }
    }
}
