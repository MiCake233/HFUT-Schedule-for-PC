package com.hfut.schedule.ui.util

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState

/**
 * 把缩放因子平滑恢复到 1f。
 *
 * 原 Android 实现用 ValueAnimator，这里改成 Compose 的挂起动画，
 * 由调用方的 CoroutineScope 驱动，Android / 桌面共用同一份实现。
 */
suspend fun smoothToOneLocal(scaleFactor: MutableFloatState) {
    restoreToOne(scaleFactor.floatValue) { scaleFactor.floatValue = it }
}

/** 兼容 MutableState<Float> 的调用方 */
suspend fun smoothToOneLocal(scaleFactor: MutableState<Float>) {
    restoreToOne(scaleFactor.value) { scaleFactor.value = it }
}

private suspend fun restoreToOne(from: Float, set: (Float) -> Unit) {
    if (from == 1f) return
    animate(
        initialValue = from,
        targetValue = 1f,
        animationSpec = tween(durationMillis = 320)
    ) { value, _ -> set(value) }
}
