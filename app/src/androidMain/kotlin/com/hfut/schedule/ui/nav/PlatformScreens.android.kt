package com.hfut.schedule.ui.nav

import androidx.compose.runtime.Composable

/**
 * Android 端平台专属页面渲染入口。
 * 共享层 Destination 请求渲染只在 Android 存在的页面时来到这里；
 * 桌面端同名入口返回 false，由 UnavailableFeature 兜底。
 */
@Composable
actual fun RenderPlatformScreen(key: String): Boolean = when (key.split("#").first()) {

        "AddEventDestination" -> {
            val p = key.split("#"); val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); val eventId = p.getOrNull(1)?.toIntOrNull() ?: -1; com.hfut.schedule.ui.screen.home.focus.funiction.AddEventScreen(vm, eventId, p.getOrNull(2)?.toBoolean() ?: false, p.getOrNull(3)?.toBoolean() ?: false)
            true
        }
        "drop_courses" -> {
            val p = key.split("#"); val name = p.getOrNull(1) ?: ""; val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.DropCourseScreen(vm, name)
            true
        }
        "select_courses_detail" -> {
            val p = key.split("#"); val index = p.getOrNull(1)?.toIntOrNull() ?: 0; val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseDetailScreen(vm, index)
            true
        }
        "control_center" -> {
            com.hfut.schedule.ui.screen.util.ControlCenterScreen()
            true
        }
        "course_live_update_detail" -> {
            CourseLiveUpdateDetailScreen()
            true
        }
        "functions_sort" -> {
            com.hfut.schedule.ui.screen.home.SearchEditScreen()
            true
        }
        "guagua_login" -> {
            com.hfut.schedule.ui.screen.shower.login.ShowerLogin()
            true
        }
        "notification_box" -> {
            com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen()
            true
        }
        "office_hall" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.school.hall.OfficeHallScreen(vm)
            true
        }
        "person_info" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonScreen(vm)
            true
        }
        "select_courses" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseScreen(vm)
            true
        }
        "stu" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.school.student.StuTodayCampusScreen(vm)
            true
        }
        "term_report" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.report.TermReportScreen(vm)
            true
        }
        "test" -> {
            TestScreen()
            true
        }
        "transfer_major" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferScreen(vm)
            true
        }
        "transfer_major_detail" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); TransferMajorDetailScreen(vm)
            true
        }
        "update_history" -> {
            UpdateHistoryScreen()
            true
        }
        "web_folder" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebUI(vm)
            true
        }
        "webvpn" -> {
            com.hfut.schedule.ui.screen.home.search.function.school.webvpn.WebVpnScreen()
            true
        }
        "xwx_home" -> {
            val vm = com.xah.navigation.util.LocalNavDependencies.current.get<com.hfut.schedule.viewmodel.network.NetWorkViewModel>(); XwxScreen(vm)
            true
        }
        "scan_qr_code" -> {
            com.hfut.schedule.ui.screen.home.search.function.school.scan.ScanScreen()
            true
        }
    else -> false
}
