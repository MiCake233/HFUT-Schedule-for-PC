package com.hfut.schedule.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import com.hfut.schedule.logic.util.other.AppInfo
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.flow.first
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * 命令行自检入口。
 *
 * 桌面端没有单元测试框架能覆盖的部分（窗口、打包、网络、凭据），都做成 `--xxx` 参数：
 * 跑一次打印机器可读的结果，由 Gradle 任务或人工核对。返回 true 表示「本次是自检，不该开窗口」。
 *
 * 这些检查必须能在**没有显示器**的环境下跑（CI / 远程），所以具体实现只依赖 headless 友好的 API。
 */
internal fun runSelfChecks(args: Array<String>): Boolean {
    // CAS 登录链路诊断（用户报「点登录一直转圈」时用来定位）
    if (args.contains("--login-probe")) {
        runLoginProbe()
        return true
    }

    // 天气图标从注册表按名字取图（替代 Android 的 resources.getIdentifier）
    if (args.contains("--verify-drawables")) {
        val codes = listOf("100", "101", "104", "305", "9999")
        codes.forEach { code ->
            val id = com.xah.common.ui.res.drawableIdOf("qweather$code")
            println("qweather$code -> id=$id" + (if (id >= 0) " name=" + com.xah.common.ui.res.Drawables.nameOf(id) else " (缺失, 会用兜底图标)"))
        }
        println("DRAWABLES_TOTAL=" + com.xah.common.ui.res.Drawables.size)
        return true
    }

    // 「写入日历」在桌面端会导出 .ics
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
        return true
    }

    // 当前周计算（学期开始时间 → 第几周）
    if (args.contains("--verify-week")) {
        kotlinx.coroutines.runBlocking {
            val term = com.hfut.schedule.logic.util.storage.kv.DataStoreManager.termStartDate.first()
            runCatching { com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.initCurrentWeekValue() }
            val week = com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.currentWeek
            println("TERM_START=$term")
            println("TODAY=" + java.time.LocalDate.now())
            println("CURRENT_WEEK=$week")
        }
        return true
    }

    // 登录状态持久化（有无本地凭据 / 能否用 TGC 换回 SESSION）
    if (args.contains("--verify-session")) {
        println("AUTOSTART=" + AutoStart.isEnabled())
        println("EXE=" + (AutoStart.currentExePath() ?: "null"))
        println("HAS_SAVED_SESSION=" + DesktopSession.hasSavedSession())
        val restored = kotlinx.coroutines.runBlocking { DesktopSession.restore() }
        println("RESTORE_RESULT=" + (restored ?: "null"))
        return true
    }

    // 运行环境标志位（正式构建不应被当成调试版）
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
        return true
    }

    // 凭据加密（DPAPI 往返 / 磁盘无明文 / 篡改拒绝 / 真实数据已加密）
    if (args.contains("--verify-secret")) {
        if (!runSecretProbe()) System.exit(2)
        return true
    }

    // 运行时 classpath 无重复类（依赖版本漂移时最容易踩的坑）
    if (args.contains("--verify-classpath")) {
        if (!runClasspathProbe()) System.exit(2)
        return true
    }

    // 备份恢复（正常恢复 / 越界条目拒绝 / 空包拒绝，全部在临时目录内进行）
    if (args.contains("--verify-backup")) {
        if (!runBackupProbe()) System.exit(2)
        return true
    }

    // 安装包完整性闸门（正确摘要应放行，错误摘要 / 非 OLE 文件应被拦截）
    if (args.contains("--verify-integrity")) {
        runIntegrityProbe()
        return true
    }

    // 更新检查（版本比较 + 资产直链 + sha256 是否解析出来）
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
        return true
    }

    // 平台功能过滤（桌面端应隐藏扫码/WebVPN/办事大厅等入口）
    if (args.contains("--verify-funcs")) {
        VALIDATED_FUNCTION_KEYS.forEach {
            println("supports($it) = " + com.hfut.schedule.logic.platform.PlatformFeatures.supports(it))
        }
        val funcs = com.hfut.schedule.ui.util.state.GlobalUiStateHolder.funcDefault
        println("FUNC_COUNT=" + funcs.size)
        println("FUNC_IDS=" + funcs.map { it.id }.joinToString(","))
        return true
    }

    // 离屏截图（自动化验收：不需要显示器也能出图）
    val snapshotIndex = args.indexOf("--snapshot")
    if (snapshotIndex >= 0) {
        runSnapshot(args, args.getOrNull(snapshotIndex + 1) ?: "desktop-home.png")
        return true
    }

    return false
}

private val VALIDATED_FUNCTION_KEYS = listOf(
    "scan_qr_code", "webvpn", "office_hall", "control_center", "term_report",
    "guagua_login", "person_info", "select_courses", "transfer_major",
    "update_history", "calendar_config", "xwx_home",
    "school_card", "grade_0", "life", "library", "washing"
)

/** 完整性闸门自检：正例放行，三种反例都必须被拦下 */
private fun runIntegrityProbe() {
    val tmpDir = System.getProperty("java.io.tmpdir")
    val oleFile = File(tmpDir, "hfut-integrity-test.msi")
    val ole = ByteArray(4096)
    ole[0] = 0xD0.toByte()
    ole[1] = 0xCF.toByte()
    ole[2] = 0x11.toByte()
    ole[3] = 0xE0.toByte()
    oleFile.writeBytes(ole)
    val realSha = java.security.MessageDigest.getInstance("SHA-256").digest(ole)
        .joinToString("") { "%02x".format(it) }
    println("MATCH=" + (DesktopUpdate.verifyLocalFile(oleFile, realSha) is DesktopUpdate.Download.Ok))
    println("WRONG_SHA_REJECTED=" +
        (DesktopUpdate.verifyLocalFile(oleFile, "0".repeat(64)) is DesktopUpdate.Download.Error))
    println("SIZE_MISMATCH_REJECTED=" +
        (DesktopUpdate.verifyLocalFile(oleFile, null, 123L) is DesktopUpdate.Download.Error))

    val plain = File(tmpDir, "hfut-integrity-test-not-msi.bin")
    plain.writeBytes(ByteArray(1024))
    println("NOT_OLE_REJECTED=" +
        (DesktopUpdate.verifyLocalFile(plain, null) is DesktopUpdate.Download.Error))
    oleFile.delete()
    plain.delete()
}

/** `--snapshot`：把界面离屏渲染成 PNG（自动化验收用） */
@OptIn(ExperimentalComposeUiApi::class)
private fun runSnapshot(args: Array<String>, out: String) {
    // 截图模式：跳过协议页，便于自动化验收
    kotlinx.coroutines.runBlocking {
        com.hfut.schedule.logic.util.storage.kv.DataStoreManager.saveEnableUse(true)
    }
    if (args.contains("--logged-in")) DesktopWindowState.loggedIn = true

    // 外观设置页单独出图
    if (args.contains("--appearance")) {
        var ok = false
        javax.swing.SwingUtilities.invokeAndWait {
            ok = snapshotOf(out) { DesktopAppTheme { DesktopAppearanceScreen(onBack = {}) } }
        }
        if (!ok) System.exit(2)
        return
    }
    // 必须跑在 AWT 事件线程上：Compose 桌面版的主线程即 EDT
    var ok = false
    javax.swing.SwingUtilities.invokeAndWait { ok = snapshotOf(out) { DesktopApp(exitApp = {}) } }
    if (!ok) System.exit(2)
}

/** 通用离屏渲染：把任意 composable 渲染成 PNG */
@OptIn(ExperimentalComposeUiApi::class)
private fun snapshotOf(path: String, content: @Composable () -> Unit): Boolean = try {
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
    LogUtil.error(t)
    false
}
