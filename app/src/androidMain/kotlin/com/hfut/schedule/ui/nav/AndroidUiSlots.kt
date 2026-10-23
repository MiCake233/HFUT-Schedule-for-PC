package com.hfut.schedule.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.rememberHazeBlurCompat
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies

/**
 * Android 端注册「平台专属子页面」实现。
 * 共享层的聚合页面（查询中心 / 生活 / 卡片 / 淋浴 / Supabase 等）通过 [UiSlots] 渲染它们，
 * 桌面端不注册，自动显示占位提示。
 *
 * 在 Application.onCreate 里调用一次。
 */
fun registerAndroidUiSlots() {
    val vmOf: @Composable (Any?) -> NetWorkViewModel = { arg ->
        (arg as? NetWorkViewModel) ?: LocalNavDependencies.current.get()
    }

    UiSlots.register("guagua_person") { arg ->
        com.hfut.schedule.ui.screen.shower.home.function.GuaGuaPersonInfoUI(
            vmOf(arg),
            rememberHazeBlurCompat()
        )
    }
    // 课表设置面板（原 MainUI 里的 BottomSheet）
    UiSlots.register("fix") {
        com.hfut.schedule.ui.screen.fix.fix.FixUI(
            innerPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            vm = LocalNavDependencies.current.get()
        )
    }
    UiSlots.register("calendar_ui_settings") {
        com.hfut.schedule.ui.screen.home.cube.screen.CalendarUISettings(true)
    }
    // 参数：内边距 / ViewModel
    UiSlots.register("home_settings") { arg ->
        val args = arg as List<*>
        com.hfut.schedule.ui.screen.home.cube.HomeSettingScreen(
            args[0] as androidx.compose.foundation.layout.PaddingValues,
            args[1] as NetWorkViewModel
        )
    }
    UiSlots.registerAction("app_env_warning") { arg ->
        com.hfut.schedule.ui.nav.window.AppEnvWarningWindow.also {
            (arg as? com.xah.floating.controller.FloatingController)?.push(it)
        }
    }
    UiSlots.updateChecker = { arg ->
        com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates(arg as NetWorkViewModel)
    }
    // 上课提醒 / 日历日程：整块只在 Android 端实现
    UiSlots.register("course_events_ui") {
        com.hfut.schedule.ui.screen.home.calendar.multi.EventUI()
    }
    // 参数：内边距
    UiSlots.register("download_ml") { arg ->
        com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI(
            arg as androidx.compose.foundation.layout.PaddingValues
        )
    }

    // 参数：ViewModel / 导航控制器
    UiSlots.registerAction("guagua_start") { arg ->
        val args = arg as List<*>
        com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.getInGuaGua(
            args[0] as NetWorkViewModel,
            args[1] as com.xah.navigation.controller.NavigationController
        ) { }
    }
}
