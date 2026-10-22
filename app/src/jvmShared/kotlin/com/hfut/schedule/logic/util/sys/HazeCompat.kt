package com.hfut.schedule.logic.util.sys

import androidx.compose.runtime.Composable
import dev.chrisbanes.haze.HazeState

/** 统一入口：创建 Haze 模糊状态（两端实现一致，这里仅做转发便于集中替换） */
@Composable
fun rememberHazeBlurCompat(): HazeState = com.hfut.schedule.ui.style.special.rememberHazeBlur()
