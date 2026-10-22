package com.hfut.schedule.ui.screen.home.search.function.school.ietp


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.TransplantListItem

import com.xah.common.ui.component.text.ScrollText
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IETP() {
    val context = LocalContext.current
    val icon = remember { Drawables.groups }
    val title = stringResource(Strings.navigation_label_ietp)
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebUrlInner(
                    context,
                    url = Constant.IETP_URL,
                    title = title,
                    icon = icon,
                )
            }
        }
    )
}
