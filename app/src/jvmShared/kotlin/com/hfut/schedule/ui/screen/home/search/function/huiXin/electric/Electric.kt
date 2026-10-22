package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource
import com.xah.common.ui.res.Strings
import com.xah.common.ui.res.stringOf
import com.xah.common.ui.res.stringResource
import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.HefeiElectricStorage
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.xah.common.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

import dev.chrisbanes.haze.HazeState
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.EleUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : NetWorkViewModel, card : Boolean, hazeState: HazeState) {
    val useHefei by DataStoreManager.useHefeiElectric.collectAsState(initial = getCampusRegion() == CampusRegion.HEFEI)
    var room by remember { mutableStateOf(stringOf(Strings.navigation_label_dormitory_electricity_bill)) }

    var showBottomSheet by remember { mutableStateOf(false) }
    if(useHefei) {
        val name by produceState<HefeiElectricStorage?>(initialValue = null) {
            value = DataStoreManager.getHefeiElectric()
        }
        room = name?.name ?: "合肥"
    } else {
        val buildingNumber = prefs.getString("BuildNumber", "0") ?: "0"

        room = when( prefs.getString("EndNumber", "0")) {
            "11"-> if(buildingNumber.toInt() > 5 )"照明" else "南边"
            "12" -> "空调"
            "21" -> if(buildingNumber.toInt() > 5 )"照明" else "北边"
            "22" -> "空调"
            else -> stringResource(Strings.navigation_label_dormitory_electricity_bill)
        }
    }


    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet
        ) {
            EleUI(vm = vm,hazeState)
        }
    }

    val f = GlobalUiStateHolder.electricValue.value ?: prefs.getString("memoryEle","0")
    val fD = f?.toDoubleOrNull() ?: 0.0
    val showRed = if(fD < 0.0) {
        // 爆红
        true
    } else if(fD > 0.0 && fD < 1.0) {
        // 爆红
        true
    } else {
        false
    }

    TransplantListItem(
        headlineContent = {
            ScrollText(
                text = if(!card) stringResource(Strings.navigation_label_dormitory_electricity_bill) else "￥${f}",
                color = if(showRed) MaterialTheme.colorScheme.error else LocalContentColor.current
            )
        },
        overlineContent = {
            ScrollText(
                text = if(!card) "￥${f}" else room,
                color = if(showRed) MaterialTheme.colorScheme.error else LocalContentColor.current
            )
        },
        leadingContent = { Icon(
            painterResource(Drawables.flash_on),
            contentDescription = "Localized description",
            tint = if(showRed) MaterialTheme.colorScheme.error else LocalContentColor.current
            ) },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )
}


