package com.hfut.schedule.ui.screen.home.cube.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.SettingsAiApiKeyPlatformDestination
import com.hfut.schedule.ui.nav.destination.SettingsHuiXinPasswordPlatformDestination
import com.hfut.schedule.ui.nav.destination.SettingsJxglstuPasswordPlatformDestination
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.launch

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSettingsScreen(
    innerPaddings : PaddingValues,
) {
    // 视频下载需要平台上下文：Android 拿窗口，桌面为 null（内部会退化到相对目录）
    val context = com.hfut.schedule.logic.util.sys.currentPlatformActivityOrNull()
    val navTopController = LocalNavController.current

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)
    )
    {
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*2))
        val enableUserTrack by DataStoreManager.enableUserTrack.collectAsState(true)

        val scope = rememberCoroutineScope()
        val navController = LocalNavController.current
        val video by produceState<String?>(initialValue = null) {
            scope.launch {
                navController.awaitTransition()
                value = checkOrDownloadVideo(context,"example_network.mp4","https://chiu-xah.github.io/videos/example_network.mp4")
            }
        }
        CustomCard (
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            video?.let {
                SimpleVideo(
                    filePath = it,
                    aspectRatio = 16/9f,
                )
            }
        }


        DividerTextExpandedWith(stringResource(Strings.network_settings_config_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                SharedContainer(
                    key = SettingsHuiXinPasswordPlatformDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        color = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(Strings.network_settings_school_card_password_title)) },
                        supportingContent = { Text(text = stringResource(Strings.network_settings_school_card_password_description)) },
                        leadingContent = { Icon(painterResource(Drawables.lock), contentDescription = "Localized description",) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsHuiXinPasswordPlatformDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsJxglstuPasswordPlatformDestination.key,
                    shape = NoneRoundShape,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        color = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(Strings.network_settings_jxglstu_password_title)) },
                        supportingContent = { Text(text = stringResource(Strings.network_settings_jxglstu_password_description)) },
                        leadingContent = { Icon(painterResource(Drawables.lock), contentDescription = "Localized description",) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsJxglstuPasswordPlatformDestination)
                        }
                    )
                }
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(Strings.network_settings_auto_refresh_login_title)) },
                    supportingContent = { Text(text = stringResource(Strings.network_settings_auto_refresh_login_description)) },
                    leadingContent = { Icon(painterResource(Drawables.rotate_auto), contentDescription = "Localized description",) },
                    trailingContent = { Switch(checked = false, enabled = false, onCheckedChange = { showDevelopingToast() })},
                    modifier = Modifier.clickable { showDevelopingToast() }
                )
                PaddingHorizontalDivider()
                SharedContainer(
                    key = SettingsAiApiKeyPlatformDestination.key,
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp)
                    ),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TransplantListItem(
                        color = MaterialTheme.colorScheme.surface,
                        headlineContent = { Text(text = stringResource(Strings.network_settings_ai_title)) },
                        supportingContent = {
                            Text(text = stringResource(Strings.network_settings_ai_description))
                        },
                        leadingContent = { Icon(
                            painterResource(Drawables.wand_stars),
                            contentDescription = "Localized description"
                        ) },
                        modifier = Modifier.clickable {
                            navTopController.push(SettingsAiApiKeyPlatformDestination)
                        }
                    )
                }
            }
        }

        DividerTextExpandedWith(stringResource(Strings.network_settings_others_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface){
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(Strings.network_settings_update_data_title)) },
                    supportingContent = { Text(text = stringResource(Strings.network_settings_update_data_description)) },
                    leadingContent = { Icon(painterResource(Drawables.cloud_upload), contentDescription = "Localized description",) },
                    trailingContent = { Switch(
                        checked = enableUserTrack,
                        onCheckedChange = {
                            scope.launch {
                                DataStoreManager.saveEnableUserTrack(!enableUserTrack)
                            }
                        }
                    ) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            DataStoreManager.saveEnableUserTrack(!enableUserTrack)
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(Strings.network_settings_refresh_login_title)) },
                    supportingContent = { Text(text = stringResource(Strings.network_settings_refresh_login_description)) },
                    leadingContent = { Icon(painterResource(Drawables.rotate_right), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {
                        refreshLogin()
                    }
                )
            }
        }
        InnerPaddingHeight(innerPaddings,false)
    }
}
