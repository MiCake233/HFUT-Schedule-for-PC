package com.hfut.schedule.ui.util

import androidx.compose.runtime.Composable

/** 当前 Composable 是否位于弹窗内容中（Android 用 LocalView 的父窗口判断；桌面端恒为 false） */
@Composable
expect fun isInDialogWindow(): Boolean