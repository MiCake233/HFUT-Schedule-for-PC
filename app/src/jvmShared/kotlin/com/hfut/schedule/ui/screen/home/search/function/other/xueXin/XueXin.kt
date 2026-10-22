package com.hfut.schedule.ui.screen.home.search.function.other.xueXin

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.stringResource
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.TransplantListItem


import com.xah.common.ui.component.text.ScrollText
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun XueXin() {
    val icon = remember { Drawables.school }
    val title = stringResource(Strings.navigation_label_chsi)
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null)

        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebUrlInner(url = Constant.XUE_XIN_URL,
                    title = title,
                    icon = icon,
                )
            }
        }
    )
}