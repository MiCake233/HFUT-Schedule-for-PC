package com.hfut.schedule.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.graphics.painter.ColorPainter
import com.hfut.schedule.logic.util.other.AppInfo
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.flow.first
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
    // 崩溃日志：写入数据目录 logs/，供「修复」窗口查看
    runCatching {
        com.hfut.schedule.logic.util.dev.CrashHandler.enableSaveLogToFile = true
        com.hfut.schedule.logic.util.dev.CrashHandler.init()
    }

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

    // 自检：当前周计算（学期开始时间 → 第几周）
    if (args.contains("--verify-week")) {
        kotlinx.coroutines.runBlocking {
            val term = com.hfut.schedule.logic.util.storage.kv.DataStoreManager.termStartDate.first()
            runCatching { com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.initCurrentWeekValue() }
            val week = com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.currentWeek
            println("TERM_START=$term")
            println("TODAY=" + java.time.LocalDate.now())
            println("CURRENT_WEEK=$week")
        }
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

    // 自检：运行环境标志位（正式构建不应被当成调试版，否则主界面会常驻「环境异常」告警）
    if (args.contains("--verify-env")) {
        println("IS_DEBUG=" + AppInfo.isDebug)
        println("SIGNATURE_VALID=" + AppInfo.isSignatureValid)
        println("IS_NORMAL_ENV=" + AppInfo.isNormalEnv)
        println("VERSION_NAME=" + AppInfo.versionName)
        println("PLATFORM_IS_DEBUG=" + com.xah.common.platform.Platform.isDebug)
        println("JPACKAGE_VERSION=" + (System.getProperty("jpackage.app-version") ?: "null"))
        println("DATA_DIR=" + com.xah.common.platform.Platform.dataDirPath())
        println("PASSWORD_SAVED=" + (com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
            .getString("Password", "").orEmpty().isNotEmpty()))
        return
    }

    // 自检：运行时 classpath 无重复类（依赖版本漂移时最容易踩的坑）
    if (args.contains("--verify-classpath")) {
        if (!runClasspathProbe()) System.exit(2)
        return
    }

    // 自检：备份恢复（正常恢复 / 越界条目拒绝 / 空包拒绝，全部在临时目录内进行）
    if (args.contains("--verify-backup")) {
        if (!runBackupProbe()) System.exit(2)
        return
    }

    // 自检：安装包完整性校验闸门（正确摘要应放行，错误摘要 / 非 OLE 文件应被拦截）
    if (args.contains("--verify-integrity")) {
        val tmp = java.io.File(System.getProperty("java.io.tmpdir"), "hfut-integrity-test.msi")
        val ole = ByteArray(4096)
        ole[0] = 0xD0.toByte(); ole[1] = 0xCF.toByte(); ole[2] = 0x11.toByte(); ole[3] = 0xE0.toByte()
        tmp.writeBytes(ole)
        val realSha = java.security.MessageDigest.getInstance("SHA-256").digest(ole)
            .joinToString("") { "%02x".format(it) }
        println("MATCH=" + (DesktopUpdate.verifyLocalFile(tmp, realSha) is DesktopUpdate.Download.Ok))
        println("WRONG_SHA_REJECTED=" +
            (DesktopUpdate.verifyLocalFile(tmp, "0".repeat(64)) is DesktopUpdate.Download.Error))
        println("SIZE_MISMATCH_REJECTED=" +
            (DesktopUpdate.verifyLocalFile(tmp, null, 123L) is DesktopUpdate.Download.Error))
        val plain = java.io.File(System.getProperty("java.io.tmpdir"), "hfut-integrity-test-not-msi.bin")
        plain.writeBytes(ByteArray(1024))
        println("NOT_OLE_REJECTED=" +
            (DesktopUpdate.verifyLocalFile(plain, null) is DesktopUpdate.Download.Error))
        tmp.delete(); plain.delete()
        return
    }

    // 自检：更新检查（版本比较 + 资产直链 + sha256 是否解析出来）
    if (args.contains("--verify-update")) {
        when (val result = DesktopUpdate.check()) {
            is DesktopUpdate.Result.UpToDate -> println("UPDATE=up-to-date current=${result.current}")
            is DesktopUpdate.Result.Available -> {
                println("UPDATE=available current=${result.current} latest=${result.latest}")
                println("ASSET_URL=${result.assetUrl}")
                println("ASSET_NAME=${result.assetName}")
                println("ASSET_SIZE=${result.assetSize}")
                println("ASSET_SHA256=${result.sha256}")
                println("PAGE_URL=${result.url}")
            }
            is DesktopUpdate.Result.Failed -> println("UPDATE=failed ${result.message}")
        }
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
        val icon = remember { loadAppIcon() }
        val trayState = rememberTrayState()
        val scope = rememberCoroutineScope()
        var windowVisible by remember { mutableStateOf(!args.contains("--minimized")) }
        var exiting by remember { mutableStateOf(false) }

        androidx.compose.runtime.DisposableEffect(Unit) {
            DesktopNotifier.sink = { title, message ->
                trayState.sendNotification(Notification(title, message))
            }
            DesktopReminders.start(scope)
            onDispose { DesktopNotifier.sink = null }
        }

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
                val closeToTray = windowPrefs.getBoolean(KEY_CLOSE_TO_TRAY, true)
                if (closeToTray && !exiting) {
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

        // 桌面课表小组件：常驻小窗口，显示今日课程
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
            androidx.compose.runtime.DisposableEffect(Unit) {
                onDispose { saveWidgetState(widgetState) }
            }
            DesktopAppTheme {
                TodayCourseWidget(onClose = { DesktopWidgetState.setShown(false) })
            }
        }
        // 修复 / 日志窗口
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
        LaunchedEffect(Unit) {
            LogUtil.debug("desktop window started")
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
