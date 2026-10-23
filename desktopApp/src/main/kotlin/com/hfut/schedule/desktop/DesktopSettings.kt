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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

/**
 * 桌面版「选项」页。
 *
 * Android 端的设置页（HomeSettingScreen）依赖小组件/快捷方式/权限等系统能力，
 * 桌面端只保留 PC 上真正有意义的入口，通过 UiSlots("home_settings") 注入到主界面里。
 */
@Composable
fun DesktopSettingsScreen(innerPadding: PaddingValues) {
    val navController = LocalNavController.current

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
            DividerTextExpandedWith("关于") {
                CustomCard {
                    TransplantListItem(
                        headlineContent = { Text("开源协议") },
                        supportingContent = { Text("GPL-3.0") },
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

