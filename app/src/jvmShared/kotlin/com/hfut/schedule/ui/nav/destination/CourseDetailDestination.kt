package com.hfut.schedule.ui.nav.destination

import com.xah.common.ui.res.Strings

import com.xah.common.ui.res.Drawables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTextbook
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLesson
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import dev.chrisbanes.haze.hazeSource

data class CourseDetailDestination(
    val lesson : JxglstuLesson,
    val courseBookData : Map<Long, JxglstuTextbook> = emptyMap(),
    val classroom : String? = null,
    val origin : String? = null,
) : NavDestination() {
    override val key = "course_detail_${lesson.id}_${origin}"
    override val description = lesson.course.nameZh
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(Strings.navigation_label_course_detail)
        val ICON = Drawables.category
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        if (!com.hfut.schedule.ui.nav.RenderPlatformScreen("course_detail")) {
            com.hfut.schedule.ui.nav.UnavailableFeature(title)
        }
    }
}