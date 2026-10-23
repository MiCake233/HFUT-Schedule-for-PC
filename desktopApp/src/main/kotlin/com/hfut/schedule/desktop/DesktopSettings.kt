package com.hfut.schedule.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppInfo
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

/**
 * 桌面版「选项」页。
 *
 * Android 端设置页依赖小组件、快捷方式、权限等系统能力，桌面端仅保留与 PC 相关的入口，
 * 通过 UiSlots("home_settings") 注入主界面。
 */
@Composable
fun DesktopSettingsScreen(innerPadding: PaddingValues) {
    val navController = LocalNavController.current
    var autoStart by remember { mutableStateOf(AutoStart.isEnabled()) }
    val sessionSaved = remember { DesktopSession.hasSavedSession() }

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
                        leadingContent = { Icon(painterResource(Drawables.info), null) },
                        modifier = Modifier.desktopClickable { navController.push(VersionInfoDestination) }
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
                            Text(
                                if (autoStart) "已写入 Windows 启动项"
                                else "登录 Windows 后自动启动本程序"
                            )
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
                        headlineContent = { Text("退出登录") },
                        supportingContent = {
                            Text(if (sessionSaved) "清除本机保存的登录状态，下次启动需重新登录" else "本机未保存登录状态")
                        },
                        leadingContent = { Icon(painterResource(Drawables.logout), null) },
                        modifier = Modifier.desktopClickable {
                            DesktopSession.clear()
                            DesktopWindowState.loggedIn = false
                            showToast("已退出登录")
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
