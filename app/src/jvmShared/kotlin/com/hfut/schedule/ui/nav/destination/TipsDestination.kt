package com.hfut.schedule.ui.nav.destination
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.text

/**
 * 使用提示页。上游 4.21.4 起从设置里链到这里，目前仍是占位。
 * Android 与桌面共用这一份，不再依赖 Android 15 的 `@RequiresApi`。
 */
object TipsDestination : NavDestination() {
    override val key: String = "settings_tips"
    override val title: UiText = text("使用提示")
    override val icon = Drawables.lightbulb

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text(title.asString()) },
                    colors = topBarTransplantColor(),
                    navigationIcon = { TopBarNavigationIcon() }
                )
            }
        ) {
            CenterScreen {
                DevelopingIcon()
            }
        }
    }
}
