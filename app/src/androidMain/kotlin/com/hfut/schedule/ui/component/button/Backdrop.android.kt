package com.hfut.schedule.ui.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.backdrops.layerBackdrop

@Composable
actual fun Modifier.layerBackdropSource(backdrop: PlatformBackdrop): Modifier =
    this.layerBackdrop(backdrop as com.kyant.backdrop.backdrops.LayerBackdrop)
