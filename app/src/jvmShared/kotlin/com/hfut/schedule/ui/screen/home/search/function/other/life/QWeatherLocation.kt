package com.hfut.schedule.ui.screen.home.search.function.other.life

import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.xah.common.logic.model.CampusRegion

/** 校区 -> 和风天气 locationID（与平台无关） */
fun getLocation(campus : CampusRegion = getCampusRegion()) : String = when(campus) {
    CampusRegion.XUANCHENG -> "101221401"
    CampusRegion.HEFEI -> "101220101"
}
