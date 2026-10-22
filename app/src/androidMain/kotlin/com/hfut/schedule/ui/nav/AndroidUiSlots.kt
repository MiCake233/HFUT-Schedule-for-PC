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

    UiSlots.register("web_lab") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebUI(vmOf(arg))
    }
    UiSlots.register("pay") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.one.pay.Pay()
    }
    UiSlots.register("today_campus") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.school.student.ToadyCampus(vmOf(arg))
    }
    UiSlots.register("electric") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.EleUI(
            vm = vmOf(arg),
            hazeState = rememberHazeBlurCompat()
        )
    }
    UiSlots.register("huixin_login") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWebScaUI(vmOf(arg))
    }
    UiSlots.register("shower") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.ShowerUI(
            vmOf(arg),
            hazeState = rememberHazeBlurCompat()
        )
    }
    UiSlots.register("guagua_person") { arg ->
        com.hfut.schedule.ui.screen.shower.home.function.GuaGuaPersonInfoUI(
            vmOf(arg),
            rememberHazeBlurCompat()
        )
    }
    UiSlots.register("campus_map") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.other.life.CampusMapScreen(
            vmOf(arg),
            androidx.compose.foundation.layout.PaddingValues(0.dp)
        )
    }
    UiSlots.register("weather") { arg ->
        com.hfut.schedule.ui.screen.home.search.function.other.life.WeatherScreen(vmOf(arg))
    }
    UiSlots.register("starter") {
        com.hfut.schedule.ui.screen.home.search.function.other.life.StarterScreen()
    }
    UiSlots.register("supabase_home") { arg ->
        com.hfut.schedule.ui.screen.supabase.home.SupabaseHomeScreen(
            vmOf(arg),
            sortType = 0,
            sortReversed = false,
            innerPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            pagerState = androidx.compose.foundation.pager.rememberPagerState { 3 }
        )
    }
    UiSlots.register("supabase_storage") { _ ->
        com.hfut.schedule.ui.screen.supabase.focus.SupabaseStorageScreen(
            androidx.compose.foundation.layout.PaddingValues(0.dp),
            rememberHazeBlurCompat()
        )
    }
    // 参数：isSupabase / isVisible / 内边距 / ViewModel
    UiSlots.register("add_event_fab") { arg ->
        val args = arg as List<*>
        com.hfut.schedule.ui.screen.home.focus.funiction.AddEventFloatButton(
            isSupabase = args[0] as Boolean,
            isVisible = args[1] as Boolean,
            innerPaddings = args[2] as androidx.compose.foundation.layout.PaddingValues,
            vm = args[3] as NetWorkViewModel
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
