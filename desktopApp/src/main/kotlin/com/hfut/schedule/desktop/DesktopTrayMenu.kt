package com.hfut.schedule.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** 托盘菜单：系统托盘的弹出菜单在 Windows 上无法渲染文字，改为自绘面板窗口。 */
object DesktopTrayMenuState {
    var visible by mutableStateOf(false)
    var onShowWindow: () -> Unit = {}
    var onQuit: () -> Unit = {}

    fun show() { visible = true }
    fun hide() { visible = false }
}

@Composable
fun DesktopTrayMenuPanel() {
    Column(modifier = Modifier.fillMaxWidth()) {
        MenuRow("Show window") {
            DesktopTrayMenuState.onShowWindow()
            DesktopTrayMenuState.hide()
        }
        HorizontalDivider()
        MenuRow("Hide to tray") { DesktopTrayMenuState.hide() }
        HorizontalDivider()
        MenuRow("Today classes panel") {
            DesktopWidgetState.setShown(!DesktopWidgetState.visible)
            DesktopTrayMenuState.hide()
        }
        HorizontalDivider()
        MenuRow("Panel always on top") {
            DesktopWidgetState.setOnTop(!DesktopWidgetState.alwaysOnTop)
            DesktopTrayMenuState.hide()
        }
        HorizontalDivider()
        MenuRow("Quit") { DesktopTrayMenuState.onQuit() }
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))
    }
}
