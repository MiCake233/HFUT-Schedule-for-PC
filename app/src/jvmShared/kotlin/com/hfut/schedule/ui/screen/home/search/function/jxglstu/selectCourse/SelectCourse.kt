package com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.stringResource
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.destination.SelectCoursesDestination

import com.xah.navigation.util.LocalNavController

import com.xah.common.ui.component.text.ScrollText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SelectCourse(
    ifSaved : Boolean,
) {
    // 选课仅 Android 提供
    if (!com.hfut.schedule.logic.platform.PlatformFeatures.supports(SelectCoursesDestination.key)) return
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = SelectCoursesDestination.title.asString()) },
        leadingContent = {
            Icon(painterResource(SelectCoursesDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            if(!ifSaved) navController.push(SelectCoursesDestination)
            else refreshLogin()
        }
    )
}

