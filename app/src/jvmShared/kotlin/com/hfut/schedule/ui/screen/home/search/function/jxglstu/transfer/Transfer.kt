package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.destination.TransferMajorDestination


import com.xah.navigation.util.LocalNavController

import com.xah.common.ui.component.text.ScrollText


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Transfer(
    ifSaved : Boolean,
){
    // 转专业页依赖 Android 专属实现，桌面端不展示入口
    if (!com.hfut.schedule.logic.platform.PlatformFeatures.supports(TransferMajorDestination.key)) return
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text =TransferMajorDestination.title.asString()) },
        leadingContent = {
            Icon(painterResource(TransferMajorDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin() else {
                navController.push(TransferMajorDestination)
            }
        }
    )
}