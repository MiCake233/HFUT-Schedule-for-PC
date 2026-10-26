package com.hfut.schedule.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppInfo
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.SettingsAppearancePlatformDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 桌面版「选项」页。Android 端设置页依赖小组件、快捷方式与权限，桌面端仅保留与 PC 相关的入口。 */
@Composable
fun DesktopSettingsScreen(innerPadding: PaddingValues) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    var autoStart by remember { mutableStateOf(AutoStart.isEnabled()) }
    var closeToTray by remember { mutableStateOf(DesktopWindowPrefs.closeToTray()) }
    var sessionSaved by remember { mutableStateOf(DesktopSession.hasSavedSession()) }
    var checking by remember { mutableStateOf(false) }
    var updateState by remember { mutableStateOf<DesktopUpdate.Result?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }

    val remindEnabled by DataStoreManager.enableLiveCourseReminder.collectAsState(initial = false)
    val remindMinutes by DataStoreManager.liveCourseReminderMinutes.collectAsState(initial = 20)

    val desktopVersion = DesktopUpdate.currentVersion() ?: AppInfo.versionName

    if (showUpdateDialog) {
        val result = updateState
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("检查更新") },
            text = {
                Text(
                    when (result) {
                        is DesktopUpdate.Result.UpToDate -> "当前已是最新版本（${result.current}）。"
                        is DesktopUpdate.Result.Available -> "发现新版本 ${result.latest}，当前版本 ${result.current}。"
                        is DesktopUpdate.Result.Failed -> "检查失败：${result.message}"
                        null -> "尚未检查。"
                    }
                )
            },
            confirmButton = {
                if (result is DesktopUpdate.Result.Available) {
                    Row {
                        TextButton(onClick = {
                            Starter.startWebUrlOuter(result.url)
                            showUpdateDialog = false
                        }) { Text("打开发布页") }
                        if (result.assetUrl != null) {
                            TextButton(enabled = !downloading, onClick = {
                                downloading = true
                                scope.launch {
                                    val outcome = DesktopUpdate.download(result) { done, total ->
                                        progress = if (total > 0) (done * 100 / total).toInt() else 0
                                    }
                                    downloading = false
                                    when (outcome) {
                                        is DesktopUpdate.Download.Ok -> {
                                            if (DesktopUpdate.launchInstaller(outcome.file)) {
                                                showToast("已开始下载并启动安装程序")
                                                showUpdateDialog = false
                                            } else {
                                                showToast("无法启动安装程序，请到 ${outcome.file.parent} 手动运行")
                                            }
                                        }
                                        is DesktopUpdate.Download.Error -> showToast(outcome.message)
                                    }
                                }
                            }) { Text(if (downloading) "下载中 $progress%" else "下载并安装") }
                        }
                    }
                } else {
                    TextButton(onClick = { showUpdateDialog = false }) { Text("关闭") }
                }
            }
        )
    }

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
                        supportingContent = { Text("桌面版 $desktopVersion · 基于上游 ${AppInfo.versionName}") },
                        leadingContent = { Icon(painterResource(Drawables.info), null) },
                        modifier = Modifier.desktopClickable { navController.push(VersionInfoDestination) }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("检查更新") },
                        supportingContent = {
                            Text(
                                when (val s = updateState) {
                                    is DesktopUpdate.Result.UpToDate -> "已是最新版本（${s.current}）"
                                    is DesktopUpdate.Result.Available -> "有新版本 ${s.latest}"
                                    is DesktopUpdate.Result.Failed -> "上次检查失败：${s.message}"
                                    null -> if (checking) "正在检查…" else "从 GitHub Releases 检查新版本"
                                }
                            )
                        },
                        leadingContent = { Icon(painterResource(Drawables.settings), null) },
                        modifier = Modifier.desktopClickable {
                            if (checking) return@desktopClickable
                            checking = true
                            scope.launch {
                                val result = withContext(Dispatchers.IO) { DesktopUpdate.check() }
                                updateState = result
                                checking = false
                                showUpdateDialog = true
                            }
                        }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("外观设置") },
                        supportingContent = { Text("配色、深色模式、动画") },
                        leadingContent = { Icon(painterResource(Drawables.palette), null) },
                        modifier = Modifier.desktopClickable {
                            navController.push(SettingsAppearancePlatformDestination)
                        }
                    )
                }
            }
            DividerTextExpandedWith("启动") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("开机自动运行") },
                        supportingContent = {
                            Text(if (autoStart) "已写入 Windows 启动项" else "登录 Windows 后自动启动本程序")
                        },
                        leadingContent = { Icon(painterResource(Drawables.play_arrow), null) },
                        trailingContent = {
                            Switch(
                                checked = autoStart,
                                onCheckedChange = { checked ->
                                    if (AutoStart.setEnabled(checked)) {
                                        autoStart = checked
                                        showToast(if (checked) "已设置为开机自动运行" else "已取消开机自动运行")
                                    } else {
                                        showToast("设置失败，请手动将本程序加入启动项")
                                    }
                                }
                            )
                        },
                        modifier = Modifier.clickable {
                            if (AutoStart.setEnabled(!autoStart)) {
                                autoStart = !autoStart
                                showToast(if (autoStart) "已设置为开机自动运行" else "已取消开机自动运行")
                            } else {
                                showToast("设置失败，请手动将本程序加入启动项")
                            }
                        }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("关闭窗口时最小化到托盘") },
                        supportingContent = {
                            Text(if (closeToTray) "关闭按钮隐藏窗口，由托盘菜单退出" else "关闭按钮直接退出程序")
                        },
                        leadingContent = { Icon(painterResource(Drawables.desktop_windows), null) },
                        trailingContent = {
                            Switch(
                                checked = closeToTray,
                                onCheckedChange = {
                                    closeToTray = it
                                    DesktopWindowPrefs.setCloseToTray(it)
                                }
                            )
                        },
                        modifier = Modifier.clickable {
                            closeToTray = !closeToTray
                            DesktopWindowPrefs.setCloseToTray(closeToTray)
                        }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("退出登录") },
                        supportingContent = {
                            Text(if (sessionSaved) "清除本机保存的登录状态，下次启动需重新登录" else "本机未保存登录状态")
                        },
                        leadingContent = { Icon(painterResource(Drawables.logout), null) },
                        modifier = Modifier.desktopClickable {
                            DesktopSession.clear()
                            DesktopWindowState.loggedIn = false
                            sessionSaved = false
                            showToast("已退出登录")
                        }
                    )
                }
            }
            DividerTextExpandedWith("桌面面板") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("今日课程面板") },
                        supportingContent = {
                            Text(if (DesktopWidgetState.visible) "已显示，常驻桌面并每 5 分钟自动刷新" else "在桌面显示今日课程（课程名与地点）")
                        },
                        leadingContent = { Icon(painterResource(Drawables.schedule), null) },
                        trailingContent = {
                            Switch(
                                checked = DesktopWidgetState.visible,
                                onCheckedChange = { DesktopWidgetState.setShown(it) }
                            )
                        },
                        modifier = Modifier.clickable {
                            DesktopWidgetState.setShown(!DesktopWidgetState.visible)
                        }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("面板始终置顶") },
                        supportingContent = {
                            Text(if (DesktopWidgetState.alwaysOnTop) "面板保持在其它窗口之上" else "面板随普通窗口切换")
                        },
                        leadingContent = { Icon(painterResource(Drawables.desktop_windows), null) },
                        trailingContent = {
                            Switch(
                                checked = DesktopWidgetState.alwaysOnTop,
                                onCheckedChange = { DesktopWidgetState.setOnTop(it) }
                            )
                        },
                        modifier = Modifier.clickable {
                            DesktopWidgetState.setOnTop(!DesktopWidgetState.alwaysOnTop)
                        }
                    )
                }
            }
            DividerTextExpandedWith("提醒") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("上课与考试提醒") },
                        supportingContent = {
                            Text(if (remindEnabled) "已开启，通过系统托盘气泡提醒" else "开启后按课表在课前提醒")
                        },
                        leadingContent = { Icon(painterResource(Drawables.schedule), null) },
                        trailingContent = {
                            Switch(
                                checked = remindEnabled,
                                onCheckedChange = { checked ->
                                    scope.launch(Dispatchers.IO) {
                                        DataStoreManager.saveLiveCourseReminder(checked)
                                    }
                                }
                            )
                        },
                        modifier = Modifier.clickable {
                            scope.launch(Dispatchers.IO) {
                                DataStoreManager.saveLiveCourseReminder(!remindEnabled)
                            }
                        }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("提前提醒时间") },
                        supportingContent = { Text("当前 $remindMinutes 分钟，点击切换") },
                        leadingContent = { Icon(painterResource(Drawables.info), null) },
                        modifier = Modifier.desktopClickable {
                            val options = listOf(5, 10, 15, 20, 30, 45, 60)
                            val index = options.indexOf(remindMinutes)
                            val next = options[if (index < 0) 0 else (index + 1) % options.size]
                            scope.launch(Dispatchers.IO) {
                                DataStoreManager.saveLiveCourseReminderMinutes(next)
                            }
                        }
                    )
                }
            }
            DividerTextExpandedWith("数据") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("备份数据") },
                        supportingContent = { Text("打包本机数据目录，由文件对话框选择保存位置") },
                        leadingContent = { Icon(painterResource(Drawables.save), null) },
                        modifier = Modifier.desktopClickable {
                            scope.launch {
                                val file = withContext(Dispatchers.IO) { DesktopBackup.backup() }
                                if (file != null) showToast("已备份到 ${file.absolutePath}") else showToast("已取消备份")
                            }
                        }
                    )
                    HorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("恢复数据") },
                        supportingContent = { Text("选择备份文件，覆盖本机数据后程序将退出") },
                        leadingContent = { Icon(painterResource(Drawables.settings_backup_restore), null) },
                        modifier = Modifier.desktopClickable {
                            scope.launch(Dispatchers.IO) { DesktopBackup.restore() }
                        }
                    )
                }
            }
            DividerTextExpandedWith("关于") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("开源协议") },
                        supportingContent = { Text("Apache-2.0") },
                        leadingContent = { Icon(painterResource(Drawables.info), null) },
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
internal fun Modifier.desktopClickable(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
