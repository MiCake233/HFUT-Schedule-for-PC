package com.hfut.schedule.ui.screen.home.calendar.multi

import com.xah.common.ui.res.Strings

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppInfo
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.CalendarConfigDestination
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.component.status.LoadingUI
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.ColumnVertical
import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res
import com.xah.navigation.anim.effect.ScaleTransitionEffect
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File



@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MultiScheduleSettings(
    ifSaved : Boolean,
    select : Int,
    onDismissed : () -> Unit,
    onSelectedChange : (Int) -> Unit,
    onShowUiSettings  : (Boolean) -> Unit,
    vm : NetWorkViewModel,
) {
    var showBottomSheet_add by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showBottomSheet_add) {
        HazeBottomSheet (onDismissRequest = { showBottomSheet_add = false },
            showBottomSheet = showBottomSheet_add,
//            expandFully = false,
//            isFullScreen = false
        ) {
            Column(modifier = Modifier) {
                HazeBottomSheetTopBar("添加课程表", isPaddingStatusBar = false)
                AddCourseUI(vm)
                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }

    var showBottomSheet_loading by remember { mutableStateOf(false) }

    if (showBottomSheet_loading) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_loading = false },
            showBottomSheet = showBottomSheet_loading,
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                HazeBottomSheetTopBar("上课提醒(以教务课表为数据源)", isPaddingStatusBar = false)
                com.hfut.schedule.ui.nav.UiSlots.Render("course_events_ui")
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }

    val navController = LocalNavController.current
    val defaultCalendar by DataStoreManager.defaultCalendar.collectAsState(initial = CourseType.JXGLSTU.code)
    val selectedColor = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    val normalColor = CardDefaults.outlinedCardColors(containerColor = cardNormalColor())
    val friendList = remember { getFriendsList() }
    val shape = MaterialTheme.shapes.medium
    val courses = remember { CourseType.entries }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        HazeBottomSheetTopBar("多课表", isPaddingStatusBar = false) {
            Row {
                FilledTonalIconButton(onClick = { showBottomSheet_add = true }) {
                    Icon(painterResource(id = Drawables.add), contentDescription = EMPTY_STRING)
                }
            }
        }
        LazyRow {
            item { Spacer(Modifier.width(APP_HORIZONTAL_DP-CARD_NORMAL_DP*2)) }
            items(courses.size) { index ->
                val item = courses[index]
                val selected = select == item.code
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clip(shape = shape)
                        .background(Color.Transparent, shape = shape)
                        .clickable {
                            onSelectedChange(item.code)
                        },
                    colors = if(selected) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ColumnVertical (
                            modifier = Modifier.align(Alignment.Center),
                        ){
                            Text(
                                item.label.asString() ,
                                fontWeight = if(selected) FontWeight.Bold else FontWeight.Light
                            )
                            if(defaultCalendar == item.code) {
                                Text(
                                    "默认" ,
                                    fontWeight = if(selected) FontWeight.Bold else FontWeight.Light,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
            // 好友课表
            items(friendList.size) { item ->
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clip(shape = shape)
                        .background(Color.Transparent, shape = shape)
                        .combinedClickable(
                            onClick = {
                                //点击加载好友课表
                                val studentId = friendList[item]?.studentId
                                studentId?.let { getFriendsCourse(it, vm) }
                                if (studentId != null) {
                                    onSelectedChange(studentId.toInt())
                                }
                            },
                            onLongClick = {
                                //s删除
                            }
                        ),
                    colors = if(select.toString() == (friendList[item]?.studentId ?: 999)) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        friendList[item]?.name?.let {
                            Text(
                                it,
                                modifier = Modifier.align(Alignment.Center),
                                fontWeight = if(select.toString() == (friendList[item]?.studentId ?: 999)) FontWeight.Bold else FontWeight.Light
                            )
                        }
                    }
                }
            }
            //添加按钮
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clip(shape = shape)
                        .background(Color.Transparent, shape = shape)
                        .clickable {
                            showBottomSheet_add = true
                            //showDialog_Add = true
                            //MyToast("请于文件管理选择他人分享的文件(json,txt)以本应用打开")
                        },
                    colors = normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(painterResource(id = Drawables.add), contentDescription = EMPTY_STRING,modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            item { Spacer(Modifier.width(APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
        }
        Spacer(Modifier.height(CARD_NORMAL_DP))
        DividerTextExpandedWith(text = "操作") {
            CustomCard (color = cardNormalColor()){
                // 课程表配置仅 Android 提供
                if (com.hfut.schedule.logic.platform.PlatformFeatures.supports(CalendarConfigDestination.key)) {
                TransplantListItem(
                    headlineContent = { Text(text = "课程表配置") },
                    supportingContent = {  Text("切换学期、切换默认课程表等配置") },
                    leadingContent = {
                        Icon(painterResource(id = Drawables.approval), contentDescription = EMPTY_STRING)
                    },
                    modifier = Modifier.clickable {
                        onDismissed()
                        navController.push(CalendarConfigDestination, effect = ScaleTransitionEffect(true, false))
                    }
                )
                PaddingHorizontalDivider()
                }
                TransplantListItem(
                    headlineContent = { Text(text = "外观调整") },
                    supportingContent = {  Text("调整课程表方格外观、背景等") },
                    leadingContent = {
                        Icon(painterResource(id = Drawables.format_paint), contentDescription = EMPTY_STRING)
                    },
                    modifier = Modifier.clickable {
                        onDismissed()
                        onShowUiSettings(true)
                    }
                )
                PaddingHorizontalDivider()
                if(select == CourseType.JXGLSTU.code) {
                    TransplantListItem(
                        headlineContent = { Text(text = "导出教务课表") },
                        supportingContent = {  Text("以文件导出教务课表") },
                        leadingContent = {
                            Icon(painterResource(id = Drawables.ios_share), contentDescription = EMPTY_STRING)
                        },
                        modifier = Modifier.clickable {
                            scope.launch {
                                // 导出教务课表：Android 走系统分享，桌面落到下载目录并打开文件夹
                                com.hfut.schedule.logic.platform.AppCapabilities.saveAndShareTextFile(
                                    "HFUT-Schedule-Share.txt",
                                    LargeStringDataManager.read(
                                        LargeStringDataManager.getJxglstuDatumKey(SemesterParser.getSemester())
                                    ) ?: "",
                                    "分享课表给他人"
                                )
                            }
                        }
                    )
                    PaddingHorizontalDivider()
                }
                val d =ifSaved && (select == CourseType.JXGLSTU.code || select == CourseType.JXGLSTU2.code)
                TransplantListItem(
                    headlineContent = { Text(text = if(d) "刷新教务课表" else "刷新课程表") },
                    supportingContent = { Text(if(d) "教务登录会失效,无法自动刷新" else "回到聚焦,等待转圈完成即完成刷新") },
                    leadingContent = {
                        Icon(painterResource(id = Drawables.rotate_right), contentDescription = EMPTY_STRING)
                    },
                    modifier = Modifier.clickable {
                        if(d)
                            refreshLogin()
                        else {
                            showToast("请回到聚焦,等待转圈完成即刷新完成")
                        }
                    }
                )
                if(select == CourseType.JXGLSTU.code) {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        supportingContent = { Text("将教务课表以日程写入到日历中") },
                        headlineContent = { Text(text = "上课提醒") },
                        leadingContent = {
                            Icon(painterResource(id = Drawables.calendar_add_on), contentDescription = EMPTY_STRING)
                        },
                        modifier = Modifier.clickable {
                            showBottomSheet_loading = true
                        }
                    )
                }
//                if(AppVersion.isPreview()) {
//                    PaddingHorizontalDivider()
//                    TransplantListItem(
//                        headlineContent = { Text(text = "恢复默认状态") },
//                        leadingContent = {
//                            Icon(painterResource(id = Drawables.delete), contentDescription = EMPTY_STRING)
//                        },
//                        modifier = Modifier.clickable {
//                            showDialog_Del = true
//                        }
//                    )
//                }
            }
        }
    }
}
