package com.hfut.schedule.desktop

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import com.hfut.schedule.logic.util.other.AppInfo
import com.xah.common.logic.util.LogUtil
import java.util.prefs.Preferences

/** 窗口尺寸/位置记忆（存到用户偏好里，Android 端不存在这个概念） */
private val windowPrefs: Preferences = Preferences.userRoot().node("com/hfut/schedule/desktop")

/**
 * 桌面端入口。
 *
 * 启动顺序有讲究，别随意调整：
 * 1. 注入版本/环境信息；
 * 2. 装好凭据加密（**必须在任何 prefs 读取之前**，否则会读到解释失败的值）；
 * 3. 装崩溃日志；
 * 4. 处理命令行自检（`--snapshot` 等），命中就直接返回，不开窗口；
 * 5. 正常启动：托盘 + 主窗口 + 今日课程面板 + 日志窗口。
 */
fun main(args: Array<String>) {
    initAppInfo()

    // 凭据加密：DPAPI 加密本机保存的 TGC / SESSION / 各类 token
    val secretsReady = runCatching { Dpapi.install() }.getOrDefault(false)
    if (secretsReady) runCatching { Dpapi.migratePlaintextSecrets() }

    runCatching {
        com.hfut.schedule.logic.util.dev.CrashHandler.enableSaveLogToFile = true
        com.hfut.schedule.logic.util.dev.CrashHandler.init()
    }

    if (runSelfChecks(args)) return

    application {
        val savedX = windowPrefs.getFloat(KEY_X, Float.NaN)
        val savedY = windowPrefs.getFloat(KEY_Y, Float.NaN)
        val state = rememberWindowState(
            size = DpSize(
                windowPrefs.getInt(KEY_WIDTH, 1180).dp,
                windowPrefs.getInt(KEY_HEIGHT, 820).dp
            ),
            position = if (savedX.isNaN() || savedY.isNaN()) {
                WindowPosition(Alignment.Center)
            } else {
                WindowPosition(savedX.dp, savedY.dp)
            }
        )
        val icon = remember { loadAppIcon() }
        val trayState = rememberTrayState()
        val scope = rememberCoroutineScope()
        var windowVisible by remember { mutableStateOf(!args.contains("--minimized")) }
        var exiting by remember { mutableStateOf(false) }

        DisposableEffect(Unit) {
            DesktopNotifier.sink = { title, message ->
                trayState.sendNotification(Notification(title, message))
            }
            DesktopReminders.start(scope)
            onDispose { DesktopNotifier.sink = null }
        }

        // 托盘菜单用 ASCII 标签：jpackage 的托盘是大字体的，中文在部分系统上会渲染成方块
        Tray(
            icon = icon ?: ColorPainter(Color(0xFFC41228)),
            state = trayState,
            tooltip = "HFUT-Schedule for PC",
            onAction = { windowVisible = true },
            menu = {
                Item("Show window", onClick = { windowVisible = true })
                Item("Hide to tray", onClick = { windowVisible = false })
                Item("Today classes panel", onClick = { DesktopWidgetState.setShown(!DesktopWidgetState.visible) })
                Item("Panel always on top", onClick = { DesktopWidgetState.setOnTop(!DesktopWidgetState.alwaysOnTop) })
                Separator()
                Item("Quit", onClick = {
                    exiting = true
                    saveWindowState(state)
                    exitApplication()
                })
            }
        )

        Window(
            onCloseRequest = {
                saveWindowState(state)
                if (DesktopWindowPrefs.closeToTray() && !exiting) {
                    windowVisible = false
                } else {
                    exiting = true
                    exitApplication()
                }
            },
            title = "HFUT 课程表 · 桌面版",
            state = state,
            visible = windowVisible,
            icon = icon
        ) {
            DesktopApp(exitApp = {
                exiting = true
                exitApplication()
            })
        }

        TodayCoursePanelWindow(icon)
        FixWindow(icon)

        LaunchedEffect(Unit) {
            LogUtil.debug("desktop window started")
        }
    }
}

/** 桌面课表面板：独立小窗口，常驻桌面显示今日课程 */
@androidx.compose.runtime.Composable
private fun TodayCoursePanelWindow(icon: Painter?) {
    val widgetX = windowPrefs.getFloat(KEY_WIDGET_X, Float.NaN)
    val widgetY = windowPrefs.getFloat(KEY_WIDGET_Y, Float.NaN)
    val widgetState = rememberWindowState(
        size = DpSize(
            windowPrefs.getInt(KEY_WIDGET_W, 380).dp,
            windowPrefs.getInt(KEY_WIDGET_H, 300).dp
        ),
        position = if (widgetX.isNaN() || widgetY.isNaN()) {
            WindowPosition(Alignment.TopEnd)
        } else {
            WindowPosition(widgetX.dp, widgetY.dp)
        }
    )
    Window(
        onCloseRequest = { DesktopWidgetState.setShown(false) },
        title = "今日课程",
        state = widgetState,
        visible = DesktopWidgetState.visible,
        alwaysOnTop = DesktopWidgetState.alwaysOnTop,
        icon = icon,
        resizable = true
    ) {
        DisposableEffect(Unit) {
            onDispose { saveWidgetState(widgetState) }
        }
        DesktopAppTheme {
            TodayCourseWidget(onClose = { DesktopWidgetState.setShown(false) })
        }
    }
}

/** 「修复 / 日志」窗口 */
@androidx.compose.runtime.Composable
private fun FixWindow(icon: Painter?) {
    Window(
        onCloseRequest = { DesktopFixState.hide() },
        title = "修复 / 日志",
        state = rememberWindowState(size = DpSize(760.dp, 580.dp)),
        visible = DesktopFixState.visible,
        icon = icon
    ) {
        DesktopAppTheme {
            DesktopFixScreen(onClose = { DesktopFixState.hide() })
        }
    }
}

private const val KEY_WIDTH = "window.width"
private const val KEY_HEIGHT = "window.height"
private const val KEY_X = "window.x"
private const val KEY_Y = "window.y"

/** 关闭按钮行为：true 为最小化到托盘，false 为退出 */
private const val KEY_CLOSE_TO_TRAY = "window.closeToTray"

/** 课表小组件窗口的位置与尺寸 */
private const val KEY_WIDGET_X = "widget.x"
private const val KEY_WIDGET_Y = "widget.y"
private const val KEY_WIDGET_W = "widget.width"
private const val KEY_WIDGET_H = "widget.height"

private fun saveWidgetState(state: androidx.compose.ui.window.WindowState) {
    runCatching {
        windowPrefs.putInt(KEY_WIDGET_W, state.size.width.value.toInt())
        windowPrefs.putInt(KEY_WIDGET_H, state.size.height.value.toInt())
        val pos = state.position
        if (pos is WindowPosition.Absolute) {
            windowPrefs.putFloat(KEY_WIDGET_X, pos.x.value)
            windowPrefs.putFloat(KEY_WIDGET_Y, pos.y.value)
        }
        windowPrefs.flush()
    }
}

/** 供设置页读写窗口行为 */
internal object DesktopWindowPrefs {
    fun closeToTray(): Boolean = windowPrefs.getBoolean(KEY_CLOSE_TO_TRAY, true)
    fun setCloseToTray(value: Boolean) {
        windowPrefs.putBoolean(KEY_CLOSE_TO_TRAY, value)
        runCatching { windowPrefs.flush() }
    }
}

/**
 * 从 classpath 读应用图标（`desktopApp/src/main/resources/app-icon.png`）。
 *
 * Android 端图标由资源系统接管；桌面端没有资源系统，直接交给 Skia 解码成
 * Compose 的 Painter，由 `Window(icon = ...)` 送到任务栏/标题栏。
 */
private fun loadAppIcon(): Painter? = runCatching {
    val bytes = object {}.javaClass.getResourceAsStream("/app-icon.png")!!.use { it.readBytes() }
    androidx.compose.ui.graphics.painter.BitmapPainter(
        org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
    )
}.getOrNull()

private fun saveWindowState(state: androidx.compose.ui.window.WindowState) {
    runCatching {
        windowPrefs.putInt(KEY_WIDTH, state.size.width.value.toInt())
        windowPrefs.putInt(KEY_HEIGHT, state.size.height.value.toInt())
        val pos = state.position
        if (pos is WindowPosition.Absolute) {
            windowPrefs.putFloat(KEY_X, pos.x.value)
            windowPrefs.putFloat(KEY_Y, pos.y.value)
        }
        windowPrefs.flush()
    }
}

/** 桌面端的版本/环境信息注入（Android 侧在 MyApplication 里做同样的事） */
private fun initAppInfo() {
    runCatching {
        AppInfo.versionName = "4.21.2.4"
        AppInfo.versionCode = 277
        AppInfo.deviceName = "Desktop"
        // 桌面端没有「调试版」概念，也没有 Android 的签名校验。这两位置对之后
        // isNormalEnv 才成立——否则主界面会一直挂着一个点了没反应的「环境异常」按钮。
        // 需要调试日志时用 -Dhfut.debug=true 启动。
        AppInfo.isDebug = System.getProperty("hfut.debug")?.toBoolean() ?: false
        AppInfo.isSignatureValid = true
        AppInfo.isRunningOnAvd = false
        AppInfo.isRunningOnWsa = false
        AppInfo.isHarmony = false
        // 桌面端按「现代 Android」处理，玻璃/模糊/着色器全部可用
        AppInfo.sdkInt = 34
    }
    // 旧版本曾把登录密码明文写进 prefs（与 Android 端共用同一个键），桌面端一律清掉
    runCatching { com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString("Password", "") }
}
