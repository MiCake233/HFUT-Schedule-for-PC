package com.hfut.schedule.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.hfut.schedule.logic.util.other.AppInfo
import com.xah.common.logic.util.LogUtil
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * 桌面端入口。
 *
 * - 带 `--snapshot <path>` 时离屏渲染一张 PNG（自动化验收用，不需要显示器）
 * - 否则打开真正的窗口
 */
fun main(args: Array<String>) {
    initAppInfo()

    val snapshotIndex = args.indexOf("--snapshot")
    if (snapshotIndex >= 0) {
        val out = args.getOrNull(snapshotIndex + 1) ?: "desktop-home.png"
        // 截图模式：可指定要渲染的阶段，便于自动化验收
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
        val windowState = rememberWindowState(width = 1180.dp, height = 820.dp)
        Window(
            onCloseRequest = ::exitApplication,
            title = "HFUT 课程表 · 桌面版",
            state = windowState
        ) {
            DesktopApp(exitApp = { exitApplication() })
        }
        LaunchedEffect(Unit) {
            LogUtil.debug("desktop window started")
        }
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
