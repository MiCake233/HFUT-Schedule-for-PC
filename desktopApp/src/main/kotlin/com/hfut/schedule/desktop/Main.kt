package com.hfut.schedule.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.hfut.schedule.logic.util.other.AppInfo
import com.xah.common.logic.util.LogUtil
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import java.util.prefs.Preferences

/** 窗口尺寸/位置记忆（存到用户偏好里，Android 端不存在这个概念） */
private val windowPrefs: Preferences = Preferences.userRoot().node("com/hfut/schedule/desktop")

/**
 * 桌面端入口。
 *
 * - 带 `--snapshot <path>` 时离屏渲染一张 PNG（自动化验收用，不需要显示器）
 *   - 可加 `--logged-in` 渲染已登录的主界面
 * - 否则打开真正的窗口
 */
fun main(args: Array<String>) {
    initAppInfo()

    // 自检：验证「写入日历」在桌面端会导出 .ics（自动化验收用）
    if (args.contains("--verify-ics")) {
        kotlinx.coroutines.runBlocking {
            com.hfut.schedule.logic.util.sys.addToCalendars(
                startDate = "2026-03-02",
                startTime = "08:00",
                endDate = "2026-03-02",
                endTime = "09:40",
                place = "主楼 201",
                title = "测试课程",
                remark = "ICS 导出验证",
                activity = null
            )
            com.hfut.schedule.logic.util.sys.addToCalendars(
                start = listOf(2026, 3, 3, 14, 0),
                end = listOf(2026, 3, 3, 15, 40),
                place = "实验楼 305",
                title = "实验课",
                remark = null,
                activity = null,
                remind = true
            )
        }
        val ics = File(File(System.getProperty("user.home"), "Downloads"), "HFUT-Schedule.ics")
        println("ICS_EXISTS=" + ics.exists() + " PATH=" + ics.absolutePath)
        if (ics.exists()) println("ICS_CONTENT_START\n" + ics.readText() + "\nICS_CONTENT_END")
        return
    }

    val snapshotIndex = args.indexOf("--snapshot")
    if (snapshotIndex >= 0) {
        val out = args.getOrNull(snapshotIndex + 1) ?: "desktop-home.png"
        // 截图模式：跳过协议页，便于自动化验收
        kotlinx.coroutines.runBlocking {
            com.hfut.schedule.logic.util.storage.kv.DataStoreManager.saveEnableUse(true)
        }
        if (args.contains("--logged-in")) DesktopWindowState.loggedIn = true
        // 必须跑在 AWT 事件线程上：Compose 桌面版的主线程即 EDT
        var ok = false
        javax.swing.SwingUtilities.invokeAndWait { ok = snapshot(out) }
        if (!ok) System.exit(2)
        return
    }

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
        Window(
            onCloseRequest = {
                saveWindowState(state)
                exitApplication()
            },
            title = "HFUT 课程表 · 桌面版",
            state = state
        ) {
            DesktopApp(exitApp = { exitApplication() })
        }
        LaunchedEffect(Unit) {
            LogUtil.debug("desktop window started")
        }
    }
}

private const val KEY_WIDTH = "window.width"
private const val KEY_HEIGHT = "window.height"
private const val KEY_X = "window.x"
private const val KEY_Y = "window.y"

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
        AppInfo.isDebug = true
        AppInfo.isSignatureValid = false
        AppInfo.isRunningOnAvd = false
        AppInfo.isRunningOnWsa = false
        AppInfo.isHarmony = false
        // 桌面端按「现代 Android」处理，玻璃/模糊/着色器全部可用
        AppInfo.sdkInt = 34
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun snapshot(path: String): Boolean = try {
    val scene = ImageComposeScene(width = 1180, height = 820, density = Density(1f)) {
        DesktopApp(exitApp = {})
    }
    val image = scene.render()
    val data = image.encodeToData(EncodedImageFormat.PNG) ?: return false
    File(path).outputStream().use { it.write(data.bytes) }
    scene.close()
    println("SNAPSHOT_OK=$path")
    true
} catch (t: Throwable) {
    println("SNAPSHOT_FAIL=${t::class.simpleName}: ${t.message}")
    t.printStackTrace()
    false
}
