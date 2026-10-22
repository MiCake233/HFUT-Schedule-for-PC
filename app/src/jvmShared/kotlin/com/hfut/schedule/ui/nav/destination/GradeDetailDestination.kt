package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.runtime.Composable
import com.hfut.schedule.network.api.model.response.html.JxglstuGrade
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeDetailScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class GradeDetailDestination(
    val bean : JxglstuGrade,
    val allAvgGpa : Float,
    val allAvgScore : Float,
    val allTotalCredits : Float
) : NavDestination() {
    override val key = "grade_detail_${bean.lessonCode}"
    override val description = bean.courseName
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(Strings.navigation_label_grade_detail)
        val ICON = Drawables.article
    }

    @Composable
    override fun Content() {
        GradeDetailScreen(bean,allAvgGpa,allAvgScore,allTotalCredits)
    }
}