package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.JxglstuProgramPlanCourse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition.JxglstuProgramCompetitionDetail
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class ProgramCompetitionDetailDestination(
    val item : JxglstuProgramCompetitionDetail,
    val programCourseMap : Map<String, JxglstuProgramPlanCourse>,
    val programTypeMap : Map<Long, String?>
) : NavDestination() {
    override val key = "program_competition_detail_${item.hashCode()}"
    override val description = when(item) {
        is JxglstuProgramCompetitionDetail.Outer -> "培养方案外课程"
        is JxglstuProgramCompetitionDetail.Inner -> item.bean.nameZh
    }
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(Strings.navigation_label_program_competition_detail)
        val ICON = Drawables.leaderboard
    }

    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformDestination(this)) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}