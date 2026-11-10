package com.hfut.schedule.desktop

import com.hfut.schedule.desktop.platform.DesktopOs
import com.hfut.schedule.desktop.platform.MacAutoStart
import com.hfut.schedule.desktop.platform.currentDesktopOs
import com.hfut.schedule.desktop.platform.trayLabel

/**
 * 开机自启动。
 *
 * Windows：当前用户注册表 `HKCU\Software\Microsoft\Windows\CurrentVersion\Run`。
 * macOS：`~/Library/LaunchAgents/com.hfut.schedule.desktop.plist`。
 * 两种都带上 [startArgument]，登录后驻留托盘或菜单栏，不抢焦点弹窗。
 * 从 IDE 直接跑 `java` 时拿不到可执行文件，开关会失败并提示手动设置。
 */
object AutoStart {

    private const val RUN_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    private const val VALUE_NAME = "HFUT-Schedule"
    private const val START_ARG = "--minimized"

    private fun exePath(): String? {
        val state = runCatching { System.getProperty("jpackage.app-path") }.getOrNull()
        if (!state.isNullOrBlank()) return state
        val cmd = runCatching { ProcessHandle.current().info().command().orElse(null) }.getOrNull()
        return if (cmd != null && cmd.endsWith(".exe", ignoreCase = true)) cmd else null
    }

    fun isEnabled(): Boolean = when (currentDesktopOs()) {
        DesktopOs.MACOS -> MacAutoStart.isEnabled()
        DesktopOs.WINDOWS -> windowsEnabled()
        else -> false
    }

    fun setEnabled(enabled: Boolean): Boolean = when (currentDesktopOs()) {
        DesktopOs.MACOS -> MacAutoStart.setEnabled(enabled)
        DesktopOs.WINDOWS -> windowsSetEnabled(enabled)
        else -> false
    }

    fun currentExePath(): String? = when (currentDesktopOs()) {
        DesktopOs.MACOS -> MacAutoStart.executablePath()
        else -> exePath()
    }

    fun startArgument(): String = START_ARG

    fun statusText(enabled: Boolean): String {
        val place = trayLabel()
        return when (currentDesktopOs()) {
            DesktopOs.MACOS -> if (enabled) {
                "已写入登录项（登录后驻留$place，点图标打开窗口）"
            } else {
                "登录 macOS 后自动启动，启动后驻留$place、不弹窗"
            }
            else -> if (enabled) {
                "已写入 Windows 启动项（开机后驻留$place，点图标打开窗口）"
            } else {
                "登录 Windows 后自动启动，启动后驻留$place、不弹窗"
            }
        }
    }

    private fun windowsEnabled(): Boolean = runCatching {
        val process = ProcessBuilder("reg", "query", RUN_KEY, "/v", VALUE_NAME)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val code = process.waitFor()
        code == 0 && output.contains(VALUE_NAME)
    }.getOrDefault(false)

    private fun windowsSetEnabled(enabled: Boolean): Boolean = runCatching {
        val path = exePath() ?: return false
        val command = if (enabled) {
            listOf("reg", "add", RUN_KEY, "/v", VALUE_NAME, "/t", "REG_SZ", "/d", "\"$path\" $START_ARG", "/f")
        } else {
            listOf("reg", "delete", RUN_KEY, "/v", VALUE_NAME, "/f")
        }
        val process = ProcessBuilder(command).redirectErrorStream(true).start()
        process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor() == 0
    }.getOrDefault(false)
}
