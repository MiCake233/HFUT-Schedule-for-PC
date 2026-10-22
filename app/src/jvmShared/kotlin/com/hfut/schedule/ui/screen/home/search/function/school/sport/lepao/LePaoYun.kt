package com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant

import com.hfut.schedule.ui.component.container.TransplantListItem


import com.xah.common.ui.component.text.ScrollText
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LePaoYun() {
    val icon = remember { Drawables.sports_volleyball }
    val title = stringResource(Strings.navigation_label_physical_fitness_test)
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null)
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier.size(30.dp),
                onClick = {
                    Starter.startAppLaunch(Starter.AppPackages.LE_PAO)
                },
            ) { Icon( painterResource(Drawables.directions_run), contentDescription = null, modifier = Modifier.size(20.dp)) }
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebUrlInner(url = Constant.PE_HOME_URL,
                    title = title,
                    icon = icon,
                    cookie = prefs.getString("PE","")
                )
            }
        }
    )
}
