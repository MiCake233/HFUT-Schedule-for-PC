package com.hfut.schedule.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.model.enumeration.BottomBarItems
import com.hfut.schedule.logic.util.other.AppInfo
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.button.HazeBottomBarDynamic
import com.hfut.schedule.ui.component.button.rememberLayerBackdrop
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.model.NavigationBarItemDataDynamic
import com.hfut.schedule.ui.model.NavigationBarItemDynamicIconModern
import com.hfut.schedule.ui.nav.destination.SettingsAppearancePlatformDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.JxglstuCourseTableTwo
import com.hfut.schedule.ui.screen.home.focus.TodayScreen
import com.hfut.schedule.ui.screen.home.search.SearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseDataSource
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.util.nav2Composable
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.res.Drawables
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import java.time.LocalDate

/** 桌面端主页签：与 Android 的 BottomBarItems 对齐，另外补了「成绩」一页（PC 上常用） */
private const val TAB_GRADE = "desktop_grade"

/**
 * 桌面端主界面。
 *
 * 结构与 Android 的 MainScreen 一致：底栏 + Nav2 平切页面 + 实时模糊背景，
 * 页面本身全部复用共享层实现（课表 / 聚焦 / 查询中心 / 成绩），
 * 只有「设置」页换成桌面版精简入口。
 */
@Composable
fun DesktopHomeScreen(vm: NetWorkViewModel) {
    val navController = rememberNavController()
    val hazeState = rememberHazeBlur()
    val backdrop = rememberLayerBackdrop()

    var today by rememberSaveable { mutableStateOf(DateTimeManager.getToday()) }
    var showAll by rememberSaveable { mutableStateOf(false) }

    val items = remember {
        listOf(
            NavigationBarItemDataDynamic(
                BottomBarItems.CALENDAR.name,
                "课程表",
                icon = { selected -> NavigationBarItemDynamicIconModern(selected, Drawables.avd_calendar) }
            ),
            NavigationBarItemDataDynamic(
                BottomBarItems.FOCUS.name,
                "聚焦",
                icon = { selected -> NavigationBarItemDynamicIconModern(selected, Drawables.avd_lightbulb) }
            ),
            NavigationBarItemDataDynamic(
                BottomBarItems.FUNCTIONS.name,
                "查询中心",
                icon = { selected -> NavigationBarItemDynamicIconModern(selected, Drawables.avd_category_search) }
            ),
            NavigationBarItemDataDynamic(
                TAB_GRADE,
                "成绩",
                icon = { selected -> NavigationBarItemDynamicIconModern(selected, Drawables.avd_deployed_code) }
            ),
            NavigationBarItemDataDynamic(
                BottomBarItems.SETTINGS.name,
                "选项",
                icon = { selected -> NavigationBarItemDynamicIconModern(selected, Drawables.avd_deployed_code_update) }
            ),
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(modifier = Modifier.bottomBarBlur(hazeState)) {
                HazeBottomBarDynamic(hazeState, items, navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomBarItems.CALENDAR.name,
            enterTransition = { AppAnimationManager.centerAnimation.enter },
            exitTransition = { AppAnimationManager.centerAnimation.exit },
            modifier = Modifier
                .hazeSource(state = hazeState)
                .backDropSource(backdrop)
        ) {
            nav2Composable(BottomBarItems.CALENDAR.name) {
                JxglstuCourseTableTwo(
                    showAll = showAll,
                    vm = vm,
                    hazeState = hazeState,
                    innerPadding = innerPadding,
                    dataSource = TotalCourseDataSource.MINE,
                    onDateChange = { newDate: LocalDate -> today = newDate },
                    today = today,
                    backGroundHaze = null,
                    onSwapShowAll = { showAll = it }
                )
            }
            nav2Composable(BottomBarItems.FOCUS.name) {
                val pagerState = rememberPagerState(pageCount = { 2 })
                TodayScreen(
                    vm = vm,
                    innerPadding = innerPadding,
                    ifSaved = false,
                    state = pagerState,
                    hazeState = hazeState
                )
            }
            nav2Composable(BottomBarItems.FUNCTIONS.name) {
                SearchScreen(
                    vm = vm,
                    ifSaved = false,
                    innerPaddings = innerPadding,
                    input = "",
                    hazeState = hazeState
                )
            }
            nav2Composable(TAB_GRADE) {
                GradeScreen(ifSaved = false, vm = vm)
            }
            nav2Composable(BottomBarItems.SETTINGS.name) {
                DesktopSettingsScreen(innerPadding)
            }
        }
    }
}

/** 桌面版设置页：只保留 PC 上真正有意义的入口 */
@Composable
private fun DesktopSettingsScreen(innerPadding: PaddingValues) {
    val navController = LocalNavController.current
    val webVpnCookie by DataStoreManager.webVpnCookies.collectAsState(initial = "")
    val webVpn = webVpnCookie.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            DividerTextExpandedWith("应用") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("版本信息") },
                        supportingContent = { Text("当前版本 ${AppInfo.versionName}") },
                        leadingContent = { androidx.compose.material3.Icon(com.xah.common.ui.res.painterResource(Drawables.info), null) },
                        modifier = Modifier.desktopClickable { navController.push(VersionInfoDestination) }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("外观设置") },
                        supportingContent = { Text("配色、深色模式、动画") },
                        leadingContent = { androidx.compose.material3.Icon(com.xah.common.ui.res.painterResource(Drawables.palette), null) },
                        modifier = Modifier.desktopClickable {
                            navController.push(SettingsAppearancePlatformDestination)
                        }
                    )
                }
            }
            DividerTextExpandedWith("网络") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("WebVPN") },
                        supportingContent = { Text(if (webVpn) "已开启" else "未开启（校园网外访问教务需要）") },
                        leadingContent = { androidx.compose.material3.Icon(com.xah.common.ui.res.painterResource(Drawables.settings), null) },
                        modifier = Modifier.desktopClickable {
                            // 桌面端暂未接入 VPN 配置页，给出提示
                            com.hfut.schedule.logic.util.sys.showToast("桌面端暂未提供该设置页，请在 Android 端开启后同步配置")
                        }
                    )
                }
            }
            DividerTextExpandedWith("关于") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("开源协议") },
                        supportingContent = { Text("GPL-3.0") },
                        leadingContent = { androidx.compose.material3.Icon(com.xah.common.ui.res.painterResource(Drawables.info), null) },
                        modifier = Modifier.desktopClickable {
                            Starter.startWebUrlOuter("https://github.com/Chiu-xaH/HFUT-Schedule")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.desktopClickable(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
