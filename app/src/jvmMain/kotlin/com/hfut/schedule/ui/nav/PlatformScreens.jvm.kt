package com.hfut.schedule.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.PlatformHooks
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.rememberLayerBackdrop
import com.hfut.schedule.ui.nav.destination.*
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.card.CardUI
import com.hfut.schedule.ui.screen.card.function.SearchBillsUI
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.grade.analysis.AverageGradeScreen
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeDetailScreen
import com.hfut.schedule.ui.screen.home.SearchEditScreen
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.hfut.schedule.ui.screen.home.cube.sub.CornerSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.LabScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventScreen
import com.hfut.schedule.ui.screen.home.search.function.community.appointment.AppointmentScreen
import com.hfut.schedule.ui.screen.home.search.function.community.bus.BusScreenV2
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRateDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRateScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.screen.LibraryBorrowedScreen
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeWashingScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchCalendarScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamNotificationsScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.ClassmatesScreen as PersonClassmatesScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramConfirmationScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.ClassmatesScreen as TotalCourseClassmatesScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.HolidayScreen
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.NewsApiScreen
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationsScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebNavigationScreen
import com.hfut.schedule.ui.screen.home.search.function.one.pay.FeeScreen
import com.hfut.schedule.ui.screen.home.search.function.other.AIScreen
import com.hfut.schedule.ui.screen.home.search.function.other.TrackScreen
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeScreen
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.AlumniScreen
import com.hfut.schedule.ui.screen.home.search.function.school.SecondClassScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionRegionScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionScreen
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomLessonsScreen
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomScreen
import com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore.DormitoryScoreScreen
import com.hfut.schedule.ui.screen.home.search.function.school.student.StuTodayCampusScreen
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.TeacherSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.school.work.WorkScreen
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.screen.news.home.NewsScreen
import com.hfut.schedule.ui.screen.shower.ShowerGuaGua
import com.hfut.schedule.ui.screen.supabase.SupabaseHome
import com.hfut.schedule.ui.screen.supabase.login.SupabaseLoginScreen
import com.hfut.schedule.ui.screen.util.ExceptionScreen
import com.hfut.schedule.ui.screen.welcome.UpdateSuccessScreen
import com.hfut.schedule.ui.screen.welcome.VersionInfoScreen
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.res
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource

/**
 * 桌面端「平台专属页面」渲染入口。
 *
 * 页面实现已位于 `jvmShared` 的，按同一份参数直接调用；
 * 依赖 Android 系统能力（相机、WebView、磁贴、PDF 导出）的返回 false，
 * 由 [UnavailableFeature] 兜底，其入口由 PlatformFeatures 在 UI 上过滤。
 * 窗口层注册了专门实现的 key（PlatformHooks.platformScreenFactory）优先。
 */
@Composable
actual fun RenderPlatformDestination(destination: NavDestination): Boolean {
    val factory = PlatformHooks.platformScreenFactory
    if (factory != null) {
        val content = factory(destination.key)
        if (content != null) {
            content()
            return true
        }
    }
    return RenderSharedDestination(destination)
}

/** 少数只有 key、没有参数对象的平台页面（设置页壳）走这个入口 */
@Composable
actual fun RenderPlatformScreen(key: String): Boolean {
    val factory = PlatformHooks.platformScreenFactory
    if (factory != null) {
        val content = factory(key.split("#").first())
        if (content != null) {
            content()
            return true
        }
    }
    return false
}

@Composable
private fun RenderSharedDestination(destination: NavDestination): Boolean = when (destination) {
    is com.hfut.schedule.ui.nav.destination.AddEventDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val eventId = if (destination.id == null || destination.id <= 0) -1 else destination.id
        AddEventScreen(vm, eventId, destination.enableCopy, destination.isSupabase)
        true
    }
    is com.hfut.schedule.ui.nav.destination.AdmissionDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AdmissionScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.AdmissionDetailDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AdmissionRegionScreen(vm, destination.bean, destination.typeStr)
        true
    }
    is com.hfut.schedule.ui.nav.destination.AgreementDestination -> {
        com.hfut.schedule.ui.screen.welcome.UseAgreementScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.AiDestination -> {
        AIScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.AllExamDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ExamNotificationsScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.AllProgramsDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramSearchScreen(vm, destination.ifSaved)
        true
    }
    is com.hfut.schedule.ui.nav.destination.AlumniDestination -> {
        AlumniScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.AverageGradeDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AverageGradeScreen(vm, destination.useUniAppData)
        true
    }
    is com.hfut.schedule.ui.nav.destination.BusDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        BusScreenV2(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.CalendarConfigDestination -> false
    is com.hfut.schedule.ui.nav.destination.ClassmatesDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        PersonClassmatesScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.ClassroomCourseTableDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassroomLessonsScreen(vm, destination.roomId, destination.name)
        true
    }
    is com.hfut.schedule.ui.nav.destination.ClassroomDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassroomScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.CommunityAppointmentDestination -> {
        AppointmentScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.CornerSettingsDestination -> {
        CornerSettingsScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.CourseClassmatesScreen -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TotalCourseClassmatesScreen(
            vm,
            destination.lessonId,
            text("${CourseClassmatesScreen.TITLE}-${destination.description}"),
            destination.isScheduled
        )
        true
    }
    is com.hfut.schedule.ui.nav.destination.CourseDetailApiDestination -> {
        CourseDetailApiScreen(destination.courseName, destination.classroom, destination.courseCode)
        true
    }
    is com.hfut.schedule.ui.nav.destination.CourseDetailDestination -> {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(destination.description) },
                    navigationIcon = { TopBarNavigationIcon() },
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems(
                    innerPadding,
                    destination.lesson,
                    destination.courseBookData
                )
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.CourseSearchApiDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        val uiState by vm.courseSearchResponse.state.collectAsState()
        val navController = LocalNavController.current
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${CourseSearchApiDestination.TITLE.asString()}: ${destination.description}") },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() },
                    actions = {
                        val canNotUse = destination.courseName == null && destination.code == null
                        val enabled = uiState is NetworkUiState.Success && !canNotUse
                        val dest = remember(enabled) {
                            if (enabled) {
                                val data = (uiState as NetworkUiState.Success).data
                                val term = if (data.isNotEmpty()) data[0].semester.id else null
                                CourseSearchTableDestination(
                                    destination.term,
                                    destination.courseName,
                                    destination.code,
                                    null,
                                    data
                                )
                            } else {
                                null
                            }
                        }
                        SharedContainer(
                            key = dest?.key,
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(end = BUTTON_END_PADDING),
                        ) {
                            LiquidButton(
                                onClick = { navController.push(dest!!) },
                                shape = NoneRoundShape,
                                backdrop = rememberLayerBackdrop(),
                                isCircle = true,
                                enabled = enabled
                            ) {
                                Icon(painterResource(Drawables.calendar), null)
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                ApiForCourseSearch(
                    vm,
                    destination.courseName,
                    destination.code,
                    destination.term,
                    innerPadding
                )
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.CourseSearchDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        CourseSearchScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.CourseSearchTableDestination -> {
        CourseSearchCalendarScreen(
            destination.term,
            destination.name,
            destination.code,
            destination.classes,
            destination.list
        )
        true
    }
    is com.hfut.schedule.ui.nav.destination.DepartmentsDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text(destination.title.asString()) },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                SchoolsUI(vm, innerPadding)
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.DormitoryDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        DormitoryScoreScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.DropCoursesDestination -> false
    is com.hfut.schedule.ui.nav.destination.ExamDestination -> {
        ExamScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.ExceptionDestination -> {
        ExceptionScreen(destination.exception)
        true
    }
    is com.hfut.schedule.ui.nav.destination.FailRateApiDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${destination.title.asString()}: ${destination.courseName}") },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                ApiToFailRate(destination.courseName, vm, innerPadding, destination.lessonCode)
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.FailRateDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        FailRateScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.FailRateDetailDestination -> {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${destination.title.asString()}: ${destination.courseName}") },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                FailRateDetailScreen(innerPadding, destination.bean)
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.FeeDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        FeeScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.FunctionsSortDestination -> {
        SearchEditScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.GradeDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        GradeScreen(destination.ifSaved, vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.GradeDetailDestination -> {
        GradeDetailScreen(
            destination.bean,
            destination.allAvgGpa,
            destination.allAvgScore,
            destination.allTotalCredits
        )
        true
    }
    is com.hfut.schedule.ui.nav.destination.GuaGuaDestination -> {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ShowerGuaGua(networkVm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.HaiLeWashingDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        HaiLeWashingScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.HaiLeWashingDetailDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        HaiLeDetailScreen(vm, destination.item, destination.description)
        true
    }
    is com.hfut.schedule.ui.nav.destination.HolidayDestination -> {
        HolidayScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.LibraryBorrowedDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        LibraryBorrowedScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.LibraryDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        LibraryScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.LifeDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        LifeScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.NewsApiDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewsApiScreen(vm, destination.keyword, destination.filteredHost)
        true
    }
    is com.hfut.schedule.ui.nav.destination.NewsDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewsScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.NotificationBoxDestination -> {
        NotificationBoxScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.NotificationsDestination -> {
        NotificationsScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.PersonInfoDestination -> false
    is com.hfut.schedule.ui.nav.destination.ProgramCompetitionDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramCompetitionScreen(vm, destination.ifSaved)
        true
    }
    is com.hfut.schedule.ui.nav.destination.ProgramCompetitionDetailDestination -> {
        ProgramCompetitionDetailScreen(
            destination.item,
            destination.programCourseMap,
            destination.programTypeMap
        )
        true
    }
    is com.hfut.schedule.ui.nav.destination.ProgramConfirmationDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramConfirmationScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.ProgramDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramScreen(vm, destination.ifSaved)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SchoolCardDestination -> {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        CardUI(networkVm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SearchBillsDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SearchBillsUI(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SecondClassDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SecondClassScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SelectCoursesDestination -> false
    is com.hfut.schedule.ui.nav.destination.SelectCoursesDetailDestination -> false
    is com.hfut.schedule.ui.nav.destination.SettingsLabDestination -> {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, MaterialTheme.colorScheme.surfaceContainer),
                    scrollBehavior = scrollBehavior,
                    title = { Text(destination.title.asString()) },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                LabScreen(innerPadding)
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.StuTodayCampusDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        StuTodayCampusScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SupabaseDestination -> {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SupabaseHome(networkVm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SupabaseLoginDestination -> {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SupabaseLoginScreen(networkVm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.SurveyDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SurveyScreen(destination.ifSaved, vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.TeacherSearchApiDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${destination.title.asString()}: ${destination.teacherName}") },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
                ApiToTeacherSearch(destination.teacherName, vm, innerPadding)
            }
        }
        true
    }
    is com.hfut.schedule.ui.nav.destination.TeacherSearchDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TeacherSearchScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.TermCoursesDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TotalCourseScreen(vm, destination.ifSaved)
        true
    }
    is com.hfut.schedule.ui.nav.destination.TrackDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TrackScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.TransferMajorDestination -> false
    is com.hfut.schedule.ui.nav.destination.TransferMajorDetailDestination -> false
    is com.hfut.schedule.ui.nav.destination.UpdateHistoryDestination -> false
    is com.hfut.schedule.ui.nav.destination.UpdateSuccessfullyDestination -> {
        UpdateSuccessScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.VersionInfoDestination -> {
        VersionInfoScreen()
        true
    }
    is com.hfut.schedule.ui.nav.destination.WebFolderDestination -> {
        WebNavigationScreen()
        true
    }
    // 网页类页面：桌面端用系统默认浏览器打开
    is com.hfut.schedule.ui.nav.destination.WebViewDestination -> {
        OpenWebPageInBrowser(destination.url, destination.description)
        true
    }
    is com.hfut.schedule.ui.nav.destination.WorkAndRestDestination -> {
        TimeTableScreen(destination.name)
        true
    }
    is com.hfut.schedule.ui.nav.destination.WorkDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        WorkScreen(vm)
        true
    }
    is com.hfut.schedule.ui.nav.destination.XiaoWuXingDestination -> false
    is com.hfut.schedule.ui.nav.destination.XiaoWuXingLoginDestination -> {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        com.hfut.schedule.ui.screen.xwx.XwxLoginScreen(vm)
        true
    }
    else -> false
}

/** 网页类页面在桌面端的实现：交给系统默认浏览器。 */
@Composable
private fun OpenWebPageInBrowser(url: String, name: String?) {
    val opened = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val open: () -> Unit = {
        opened.value = runCatching {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(java.net.URI(url)); true
            } else {
                false
            }
        }.getOrDefault(false)
    }
    androidx.compose.runtime.LaunchedEffect(url) { open() }
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(Drawables.publics),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = name ?: "网页",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (opened.value) "已在系统浏览器中打开该页面。" else "点击下方按钮在系统浏览器中打开该页面。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.material3.Button(onClick = { open() }) {
                Text("在浏览器中打开")
            }
        }
    }
}
