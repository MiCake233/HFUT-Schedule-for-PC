package com.hfut.schedule.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.graphics.toComposeImageBitmap
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

    // 自检：CAS 登录链路诊断（用户报「点登录一直转圈」时用来定位）
    if (args.contains("--login-probe")) {
        runLoginProbe()
        return
    }

    // 自检：天气图标从注册表按名字取图（替代 Android 的 resources.getIdentifier）
    if (args.contains("--verify-drawables")) {
        val codes = listOf("100", "101", "104", "305", "9999")
        codes.forEach { code ->
            val id = com.xah.common.ui.res.drawableIdOf("qweather$code")
            println("qweather$code -> id=$id" + (if (id >= 0) " name=" + com.xah.common.ui.res.Drawables.nameOf(id) else " (缺失, 会用兜底图标)"))
        }
        println("DRAWABLES_TOTAL=" + com.xah.common.ui.res.Drawables.size)
        return
    }

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

    // 自检：登录状态持久化（有无本地凭据 / 恢复结果）
    if (args.contains("--verify-session")) {
        println("AUTOSTART=" + AutoStart.isEnabled())
        println("EXE=" + (AutoStart.currentExePath() ?: "null"))
        println("HAS_SAVED_SESSION=" + DesktopSession.hasSavedSession())
        val restored = kotlinx.coroutines.runBlocking { DesktopSession.restore() }
        println("RESTORE_RESULT=" + (restored ?: "null"))
        return
    }

    // 自检：平台功能过滤（桌面端应隐藏扫码/WebVPN/办事大厅等入口）
    if (args.contains("--verify-funcs")) {
        val keys = listOf(
            "scan_qr_code", "webvpn", "office_hall", "control_center", "term_report",
            "guagua_login", "person_info", "select_courses", "transfer_major",
            "update_history", "calendar_config", "xwx_home",
            "school_card", "grade_0", "life", "library", "washing"
        )
        keys.forEach { println("supports($it) = " + com.hfut.schedule.logic.platform.PlatformFeatures.supports(it)) }
        val funcs = com.hfut.schedule.ui.util.state.GlobalUiStateHolder.funcDefault
        println("FUNC_COUNT=" + funcs.size)
        println("FUNC_IDS=" + funcs.map { it.id }.joinToString(","))
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
        // 外观设置页单独出图（自动化验收用）
        if (args.contains("--appearance")) {
            var ok2 = false
            javax.swing.SwingUtilities.invokeAndWait {
                ok2 = snapshotOf(out) {
                    DesktopAppTheme { DesktopAppearanceScreen(onBack = {}) }
                }
            }
            if (!ok2) System.exit(2)
            return
        }
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
            state = state,
            // 窗口/任务栏图标，与安装包 MSI 用同一个图形资源
            icon = remember { loadAppIcon() }
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

/**
 * 从 classpath 读应用图标（`desktopApp/src/main/resources/app-icon.png`）。
 *
 * Android 端图标由资源系统接管；桌面端没有资源系统，直接交给 Skia 解码成
 * Compose 的 Painter，由 `Window(icon = ...)` 送到任务栏/标题栏。
 */
private fun loadAppIcon(): androidx.compose.ui.graphics.painter.Painter? = runCatching {
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
private fun snapshot(path: String): Boolean = snapshotOf(path) {
    DesktopApp(exitApp = {})
}

/** 通用离屏渲染：把任意 composable 渲染成 PNG */
@OptIn(ExperimentalComposeUiApi::class)
private fun snapshotOf(path: String, content: @androidx.compose.runtime.Composable () -> Unit): Boolean = try {
    val scene = ImageComposeScene(width = 1180, height = 820, density = Density(1f)) {
        content()
    }
    // 先渲染一帧把 LaunchedEffect 跑起来（网络图片、异步状态都在这里发起）
    scene.render()
    // 离屏场景没有自己的事件循环：必须反复 render() 才会推进协程/动画，
    // 否则验证码这类异步加载的图片永远停在占位图。
    val wait = System.getProperty("hfut.snapshot.wait")?.toLongOrNull() ?: 0L
    if (wait > 0) {
        val step = 150L
        var elapsed = 0L
        while (elapsed < wait) {
            Thread.sleep(step)
            scene.render()
            elapsed += step
        }
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
