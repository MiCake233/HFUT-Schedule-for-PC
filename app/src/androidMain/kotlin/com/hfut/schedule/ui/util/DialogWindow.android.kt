package com.hfut.schedule.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
actual fun isInDialogWindow(): Boolean {
    val view = LocalView.current
    return view.parent is DialogWindowProvider
}