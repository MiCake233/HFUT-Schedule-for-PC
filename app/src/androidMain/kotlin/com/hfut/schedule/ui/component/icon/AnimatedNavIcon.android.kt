package com.hfut.schedule.ui.component.icon

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.hfut.schedule.R
import com.xah.common.ui.res.Drawables
import com.xah.common.ui.res.painterResource

/**
 * Android 侧：把注册表下标映射回 Android 的 R.drawable.avd_*，走真正的矢量动画。
 */
@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
actual fun rememberAnimatedNavIcon(iconId: Int, atEnd: Boolean): Painter {
    val androidRes = when (iconId) {
        Drawables.avd_calendar -> Drawables.avd_calendar
        Drawables.avd_category_search -> Drawables.avd_category_search
        Drawables.avd_deployed_code -> Drawables.avd_deployed_code
        Drawables.avd_deployed_code_update -> Drawables.avd_deployed_code_update
        Drawables.avd_lightbulb -> Drawables.avd_lightbulb
        else -> null
    }
    return if (androidRes != null) {
        val image = AnimatedImageVector.animatedVectorResource(androidRes)
        rememberAnimatedVectorPainter(
            animatedImageVector = image,
            atEnd = atEnd
        )
    } else {
        painterResource(iconId)
    }
}
