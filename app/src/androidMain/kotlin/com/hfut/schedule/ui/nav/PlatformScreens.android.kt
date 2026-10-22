package com.hfut.schedule.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.Constant.PARTY_BRANCH_URL
import com.hfut.schedule.network.api.model.response.html.JxglstuGrade
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.JxglstuProgramPlanCourse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition.JxglstuProgramCompetitionDetail
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.rememberLayerBackdrop
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.nav.effect.ControlCenterTransitionEffect
import com.hfut.schedule.ui.screen.card.CardUI
import com.hfut.schedule.ui.screen.card.function.SearchBillsUI
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.grade.analysis.AverageGradeScreen
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeDetailScreen
import com.hfut.schedule.ui.screen.home.SearchEditScreen
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.hfut.schedule.ui.screen.home.cube.UpdateContents
import com.hfut.schedule.ui.screen.home.cube.screen.CalendarSettingsUI
import com.hfut.schedule.ui.screen.home.cube.sub.CornerSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.LabScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventScreen
import com.hfut.schedule.ui.screen.home.search.function.community.appointment.AppointmentScreen
import com.hfut.schedule.ui.screen.home.search.function.community.bus.BusScreen
import com.hfut.schedule.ui.screen.home.search.function.community.bus.BusScreenV2
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.screen.LibraryBorrowedScreen
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeWashingScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamNotificationsScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.ClassmatesScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.DropCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.ClassmatesScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferScreen
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.HolidayScreen
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.NewsApiScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebNavigationScreen
import com.hfut.schedule.ui.screen.home.search.function.one.pay.FeeScreen
import com.hfut.schedule.ui.screen.home.search.function.other.AIScreen
import com.hfut.schedule.ui.screen.home.search.function.other.TrackScreen
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeScreen
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.AlumniScreen
import com.hfut.schedule.ui.screen.home.search.function.school.SecondClassScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionScreen
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomLessonsScreen
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomScreen
import com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore.DormitoryScoreScreen
import com.hfut.schedule.ui.screen.home.search.function.school.hall.OfficeHallScreen
import com.hfut.schedule.ui.screen.home.search.function.school.scan.ScanScreen
import com.hfut.schedule.ui.screen.home.search.function.school.student.StuTodayCampusScreen
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.WebVpnScreen
import com.hfut.schedule.ui.screen.home.search.function.school.work.WorkScreen
import com.hfut.schedule.ui.screen.report.TermReportScreen
import com.hfut.schedule.ui.screen.shower.ShowerGuaGua
import com.hfut.schedule.ui.screen.supabase.SupabaseHome
import com.hfut.schedule.ui.screen.util.ControlCenterScreen
import com.hfut.schedule.ui.screen.util.ExceptionScreen
import com.hfut.schedule.ui.screen.welcome.UpdateSuccessScreen
import com.hfut.schedule.ui.screen.welcome.UseAgreementScreen
import com.hfut.schedule.ui.screen.welcome.VersionInfoScreen
import com.hfut.schedule.ui.screen.xwx.XwxMainScreen
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.theme.pureMaskColor
import com.hfut.schedule.ui.util.loadBitmap
import com.hfut.schedule.ui.util.pickColorFromTop
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.style.color.TransparentSystemBars
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.res
import com.xah.common.ui.util.text
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android 端平台专属页面渲染入口。
 * 共享层 Destination 把 Content() 委托到这里；桌面端同名入口返回 false，
 * 由 UnavailableFeature 显示"该功能依赖 Android 系统能力"。
 *
 * 注意：带构造参数的页面（如 AddEvent）用 key 尾部携带参数，见各 Destination 的实现。
 */
@Composable
actual fun RenderPlatformScreen(key: String): Boolean = when (key.split("#").first()) {

        "AddEventDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    val eventId = if(id == null || id <= 0) {
                        -1
                    } else {
                        id
                    }
                    AddEventScreen(vm,eventId,enableCopy,isSupabase)
            true
        }
        "admission" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    AdmissionScreen(vm)
            true
        }
        "AgreementDestination" -> {
            UseAgreementScreen()
            true
        }
        "ai" -> {
            AIScreen()
            true
        }
        "all" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ExamNotificationsScreen(vm)
            true
        }
        "AllProgramsDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ProgramSearchScreen(vm,ifSaved)
            true
        }
        "alumni" -> {
            AlumniScreen()
            true
        }
        "AverageGradeDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    AverageGradeScreen(vm,useUniAppData)
            true
        }
        "bus" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    BusScreenV2(vm)
            true
        }
        "calendar" -> {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    val hazeState = rememberHazeBlur()
            
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MediumTopAppBar(
                                modifier = Modifier.topBarBlur(hazeState, MaterialTheme.colorScheme.surfaceContainer),
                                scrollBehavior = scrollBehavior,
                                title = { Text(title.asString()) },
                                colors = topBarTransplantColor(),
                                navigationIcon = {
                                    TopBarNavigationIcon()
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(hazeState)
                        ) {
                            LazyColumn {
                                item { InnerPaddingHeight(innerPadding,true) }
                                item { CalendarSettingsUI() }
                                item { InnerPaddingHeight(innerPadding,false) }
                            }
                        }
                    }
            true
        }
        "classmates" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ClassmatesScreen(vm)
            true
        }
        "classroom" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ClassroomLessonsScreen(vm,roomId,name)
            true
        }
        "ClassroomDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ClassroomScreen(vm)
            true
        }
        "community" -> {
            AppointmentScreen()
            true
        }
        "control" -> {
            ControlCenterScreen()
            true
        }
        "settings" -> {
            CornerSettingsScreen()
            true
        }
        "CourseClassmatesScreen" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ClassmatesScreen(vm,lessonId,text("$TITLE-$description"),isScheduled)
            true
        }
        "course" -> {
            CourseDetailApiScreen(courseName, classroom, courseCode)
            true
        }
        "dormitory" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    DormitoryScoreScreen(vm)
            true
        }
        "drop" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    DropCourseScreen(vm,courseId,name)
            true
        }
        "ExamDestination" -> {
            ExamScreen()
            true
        }
        "exception" -> {
            ExceptionScreen(exception)
            true
        }
        "fail" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    val hazeState = rememberHazeBlur()
            
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MediumTopAppBar(
                                modifier = Modifier.topBarBlur(hazeState),
                                scrollBehavior = scrollBehavior,
                                title = { Text("${title.asString()}: $courseName") },
                                colors = topBarTransplantColor(),
                                navigationIcon = {
                                    TopBarNavigationIcon()
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(hazeState)
                        ) {
                            ApiToFailRate(courseName, vm, innerPadding, lessonCode)
                        }
                    }
            true
        }
        "fee" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    FeeScreen(vm)
            true
        }
        "functions" -> {
            SearchEditScreen()
            true
        }
        "GradeDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    GradeScreen(ifSaved,vm)
            true
        }
        "grade" -> {
            GradeDetailScreen(bean,allAvgGpa,allAvgScore,allTotalCredits)
            true
        }
        "guagua" -> {
            val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ShowerGuaGua(networkVm)
            true
        }
        "washing" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    HaiLeWashingScreen(vm)
            true
        }
        "holiday" -> {
            HolidayScreen()
            true
        }
        "LibraryBorrowedDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    LibraryBorrowedScreen(vm)
            true
        }
        "library" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    LibraryScreen(vm)
            true
        }
        "life" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    LifeScreen(vm)
            true
        }
        "news" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    NewsApiScreen(vm, keyword,filteredHost)
            true
        }
        "notification" -> {
            NotificationBoxScreen()
            true
        }
        "office" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    OfficeHallScreen(vm)
            true
        }
        "person" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    PersonScreen(vm)
            true
        }
        "ProgramCompetitionDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ProgramCompetitionScreen(vm,ifSaved)
            true
        }
        "program" -> {
            ProgramCompetitionDetailScreen(item,programCourseMap,programTypeMap)
            true
        }
        "ProgramDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ProgramScreen(vm,ifSaved)
            true
        }
        "scan" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    ScanScreen(vm)
            true
        }
        "school" -> {
            val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    CardUI(networkVm)
            true
        }
        "search" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    SearchBillsUI(vm)
            true
        }
        "second" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    SecondClassScreen(vm)
            true
        }
        "select" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    SelectCourseScreen(vm)
            true
        }
        "SettingsLabDestination" -> {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    val hazeState = rememberHazeBlur()
            
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MediumTopAppBar(
                                modifier = Modifier.topBarBlur(hazeState, MaterialTheme.colorScheme.surfaceContainer),
                                scrollBehavior = scrollBehavior,
                                title = { Text(title.asString()) },
                                colors = topBarTransplantColor(),
                                navigationIcon = {
                                    TopBarNavigationIcon()
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(hazeState)
                        ) {
                            LabScreen(innerPadding)
                        }
                    }
            true
        }
        "stu" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    StuTodayCampusScreen(vm)
            true
        }
        "supabase" -> {
            val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    SupabaseHome(networkVm)
            true
        }
        "survey" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    // TODO
            true
        }
        "SurveyDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    SurveyScreen(ifSaved,vm)
            true
        }
        "teacher" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    val hazeState = rememberHazeBlur()
            
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MediumTopAppBar(
                                modifier = Modifier.topBarBlur(hazeState),
                                scrollBehavior = scrollBehavior,
                                title = { Text("${title.asString()}: $teacherName") },
                                colors = topBarTransplantColor(),
                                navigationIcon = {
                                    TopBarNavigationIcon()
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(hazeState)
                        ) {
                            ApiToTeacherSearch(teacherName, vm, innerPadding)
                        }
                    }
            true
        }
        "TermCoursesDestination" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    TotalCourseScreen(vm,ifSaved)
            true
        }
        "term" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    TermReportScreen(vm)
            true
        }
        "test" -> {
            val hazeState = rememberHazeBlur()
                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                    val backdrop = rememberLayerBackdrop()
            
                    val customBackground by DataStoreManager.customBackground.collectAsState(initial = EMPTY_STRING)
                    val useCustomBackground = customBackground != EMPTY_STRING
                    val pureMaskColor = pureMaskColor()
                    var inFullScreen by remember { mutableStateOf(false) }
                    var downDrag by remember { mutableFloatStateOf(0f) }
                    val navController = LocalNavController.current
                    val surfaceColor = MaterialTheme.colorScheme.surface
            
                    LaunchedEffect(useCustomBackground) {
                        inFullScreen = useCustomBackground
                    }
            
                    Scaffold (
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            if(!inFullScreen) {
                                Column(
                                    modifier = Modifier.topBarBlur(hazeState),
                                ) {
                                    MediumTopAppBar(
                                        scrollBehavior = scrollBehavior,
                                        colors = topBarTransplantColor().copy(
                                            titleContentColor = pureMaskColor
                                        ),
                                        title = { Text(title.asString()) },
                                        navigationIcon = {
                                            TopBarNavigationIcon(tint = pureMaskColor)
                                        },
                                    )
                                }
                            }
                        },
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .backDropSource(backdrop)
                                .hazeSource(hazeState)
                                .fillMaxSize()
                        ) {
                            // 背景图层
                            if (useCustomBackground) {
                                // 状态栏反色
                                val file = remember(customBackground) { File(customBackground) }
                                val color by produceState<Int?>(initialValue = null) {
                                    value = withContext(Dispatchers.IO) {
                                        val bitmap = loadBitmap(file) ?: return@withContext null
                                        val result = pickColorFromTop(bitmap)
                                        bitmap.recycle()
                                        result
                                    }
                                }
                                TransparentSystemBars(color?.let { it1 -> Color(it1) })
                                GlideImage(
                                    model = file,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            inFullScreen = !inFullScreen
                                        }
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDrag = { _, dragAmount ->
                                                    val dx = dragAmount.x
                                                    val dy = dragAmount.y
            
                                                    // 下滑唤醒启动台 TODO 后期做跟手
                                                    if (dy > 0 && kotlin.math.abs(dy) > kotlin.math.abs(dx)) {
                                                        downDrag += dy
            
                                                        if (downDrag >= 300f) {
                                                            downDrag = 0f
                                                            inFullScreen = true
                                                            navController.push(
                                                                destination = ControlCenterDestination,
                                                                effect = ControlCenterTransitionEffect(compositeOverColor = surfaceColor),
                                                                launchMode = LaunchMode.Push(keepPreviousAlive = true)
                                                            )
                                                        }
                                                    }
                                                    // 向右
                                                    else if (dx > 0 && kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                                                        downDrag = 0f
                                                    }
                                                },
                                                onDragEnd = {
                                                    downDrag = 0f
                                                },
                                                onDragCancel = {
                                                    downDrag = 0f
                                                }
                                            )
                                        }
                                )
                            }
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                InnerPaddingHeight(innerPadding,true)
            
                                InnerPaddingHeight(innerPadding,false)
                            }
                        }
                    }
            true
        }
        "track" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    TrackScreen(vm)
            true
        }
        "transfer" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    TransferScreen(vm)
            true
        }
        "update" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    UpdateContents(vm)
            true
        }
        "UpdateSuccessfullyDestination" -> {
            UpdateSuccessScreen()
            true
        }
        "version" -> {
            VersionInfoScreen()
            true
        }
        "web" -> {
            WebNavigationScreen()
            true
        }
        "WebViewDestination" -> {
            //        val url = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.URL.argName) ?: return@transitionComposable
            //        val cookies = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.COOKIES.argName)
            //        val title = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.TITLE.argName) ?: getPureUrl(url)
            //        val icon = backStackEntry.arguments?.getInt(AppNavRoute.WebView.Args.ICON.argName)
            //
            //        WebViewScreenForNavigation(
            //            url,
            //            title,
            //            icon,
            //            cookies,
            //            navController,
            //            drawerState
            //        ) { containerColor = it }
            //        WebViewScreenForNavigation(url, name, icon, cookies)
            true
        }
        "webvpn" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    WebVpnScreen(vm)
            true
        }
        "WorkAndRestDestination" -> {
            TimeTableScreen(name)
            true
        }
        "work" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    WorkScreen(vm)
            true
        }
        "xwx" -> {
            val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
                    XwxMainScreen(vm)
            true
        }
        "platform_settings_appearance" -> {
            com.hfut.schedule.ui.nav.destination.SettingsAppearanceDestination.Content()
            true
        }
        "platform_settings_jxglstu_password" -> {
            com.hfut.schedule.ui.nav.destination.SettingsJxglstuPasswordDestination.Content()
            true
        }
    else -> false
}
