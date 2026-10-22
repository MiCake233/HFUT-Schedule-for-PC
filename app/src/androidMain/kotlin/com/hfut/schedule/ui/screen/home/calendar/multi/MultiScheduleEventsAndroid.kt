package com.hfut.schedule.ui.screen.home.calendar.multi

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppInfo
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.CourseLiveUpdateScheduler
import com.hfut.schedule.logic.util.sys.PermissionSet.checkAndRequestNotificationPermission
import com.hfut.schedule.logic.util.sys.addCourseToEvent
import com.hfut.schedule.logic.util.sys.delAllCourseEvent
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.ui.component.status.LoadingUI
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

/*
 * 「上课提醒 / 日历日程」这一整块只在 Android 端实现：
 * Live Activity 调度、通知权限、系统日历写入都依赖 Android 系统能力。
 * 桌面端通过 UiSlots("course_events_ui") 显示占位提示。
 */
fun saveTextToFile( fileName: String, content: String) {
    val file = File(MyApplication.context.getExternalFilesDir(null), fileName)
    file.writeText(content)
}

fun shareTextFile(fileName: String) {
    val file = File(MyApplication.context.getExternalFilesDir(null), fileName)
    val uri = FileProvider.getUriForFile(MyApplication.context, "${com.hfut.schedule.logic.platform.AppCapabilities.packageName()}.provider", file)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    MyApplication.context.startActivity(Intent.createChooser(shareIntent, "分享课表给他人").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}


@Composable
internal fun EventUI() {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val time by DataStoreManager.liveCourseReminderMinutes.collectAsState(initial = 20)
    val enableLiveCourseReminder by DataStoreManager.enableLiveCourseReminder.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    if(loading) {
        LoadingUI("勿动稍等")
    } else {
        CardListItem(
            overlineContent = {
                Text("提醒时间")
            },
            headlineContent = {
                Text("上课前${time}min")
            },
            trailingContent = {
                Row {
                    FilledTonalIconButton(
                        onClick = {
                            scope.launch {
                                DataStoreManager.saveLiveCourseReminderMinutes(time + 5)
                                if (enableLiveCourseReminder) {
                                    CourseLiveUpdateScheduler.cancelAll(context)
                                    CourseLiveUpdateScheduler.scheduleAll(context)
                                }
                            }
                        }
                    ) {
                        Icon(painterResource(Drawables.add),null)
                    }
                    time.let {
                        if(it >= 5) {
                            FilledTonalIconButton(
                                onClick = {
                                    scope.launch {
                                        DataStoreManager.saveLiveCourseReminderMinutes(time - 5)
                                        if (enableLiveCourseReminder) {
                                            CourseLiveUpdateScheduler.cancelAll(context)
                                            CourseLiveUpdateScheduler.scheduleAll(context)
                                        }
                                    }
                                }
                            ) {
                                Icon(painterResource(Drawables.remove),null)
                            }
                        }
                    }
                }
            },
            leadingContent = { Icon(painterResource(Drawables.schedule),null) },
            modifier = Modifier.clickable {
                scope.launch {
                    DataStoreManager.saveLiveCourseReminderMinutes(20)
                    if (enableLiveCourseReminder) {
                        CourseLiveUpdateScheduler.cancelAll(context)
                        CourseLiveUpdateScheduler.scheduleAll(context)
                    }
                }
            }
        )
        DividerTextExpandedWith("实时通知") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = {
                        Text(if (enableLiveCourseReminder) "Android 16 Live Activity 已开启" else "开启 Android 16 Live Activity 提醒")
                    },
                    supportingContent = {
                        Text("使用上方提醒时间，上课前${time}min弹出并显示教室；低版本降级为普通通知")
                    },
                    leadingContent = { Icon(painterResource(Drawables.notifications), null) },
                    modifier = Modifier.clickable {
                        activity?.let { checkAndRequestNotificationPermission(it) }
                        scope.launch {
                            if (enableLiveCourseReminder) {
                                CourseLiveUpdateScheduler.cancelAll(context)
                                DataStoreManager.saveLiveCourseReminder(false)
                                showToast("已关闭 Live Activity 提醒")
                            } else if (CourseLiveUpdateScheduler.canPostNotification(context)) {
                                val count = CourseLiveUpdateScheduler.scheduleAll(context)
                                CourseLiveUpdateScheduler.showCurrentWindowCourses(context)
                                DataStoreManager.saveLiveCourseReminder(true)
                                showToast("已开启 Live Activity 提醒，共调度${count}节课")
                            } else {
                                showToast("请先授予通知权限")
                            }
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = {
                        Text("刷新 Live Activity 提醒")
                    },
                    supportingContent = {
                        Text("课表或提醒时间变化后重新调度后台提醒")
                    },
                    leadingContent = { Icon(painterResource(Drawables.rotate_right), null) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            CourseLiveUpdateScheduler.cancelAll(context)
                            val count = CourseLiveUpdateScheduler.scheduleAll(context)
                            CourseLiveUpdateScheduler.showCurrentWindowCourses(context)
                            DataStoreManager.saveLiveCourseReminder(true)
                            showToast("已刷新 Live Activity 提醒，共调度${count}节课")
                        }
                    }
                )
            }
        }
        DividerTextExpandedWith("日历日程") {
            CardListItem(
                headlineContent = {
                    Text("清空+导入")
                },
                supportingContent = {
                    Text("将目前的教务课表写入到日程")
                },
                leadingContent = { Icon(painterResource(Drawables.event_upcoming),null) },
                modifier = Modifier.clickable {
                    activity?.let {
                        scope.launch {
                            async { loading = true }.await()
                            async { delAllCourseEvent(activity = it) }.await()
                            async { addCourseToEvent(activity = it,time) }.await()
                            launch { loading = false }
                        }
                    }
                }
            )
            CardListItem(
                headlineContent = {
                    Text("清空")
                },
                supportingContent = {
                    Text("清空导入的日程")
                },
                leadingContent = { Icon(painterResource(Drawables.event_busy),null) },
                modifier = Modifier.clickable {
                    activity?.let {
                        scope.launch {
                            async { loading = true }.await()
                            async { delAllCourseEvent(activity = it) }.await()
                            launch { loading = false }
                        }
                    }
                }
            )
            CardListItem(
                headlineContent = {
                    Text("导出为ics")
                },
                supportingContent = {
                    Text("将目前的教务课表写入到日程ics文件")
                },
                leadingContent = { Icon(painterResource(Drawables.attach_file),null) },
                modifier = Modifier.clickable {
                    showToast("正在开发")
                }
            )
        }
    }
}

