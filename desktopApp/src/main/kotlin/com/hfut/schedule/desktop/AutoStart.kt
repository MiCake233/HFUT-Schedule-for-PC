package com.hfut.schedule.desktop

/**
 * Windows 开机自启动：写入/删除 `HKCU\Software\Microsoft\Windows\CurrentVersion\Run`。
 * 只对已安装/免安装的 exe 生效；用 IDE 直接跑 main() 时不注册。
 */
object AutoStart {

    private const val RUN_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
    private const val VALUE_NAME = "HFUT-Schedule"

    private fun exePath(): String? {
        val state = runCatching { System.getProperty("jpackage.app-path") }.getOrNull()
        if (!state.isNullOrBlank()) return state
        val cmd = runCatching { ProcessHandle.current().info().command().orElse(null) }.getOrNull()
        return if (cmd != null && cmd.endsWith(".exe", ignoreCase = true)) cmd else null
    }

    /** 是否已加入启动项 */
    fun isEnabled(): Boolean = runCatching {
        val process = ProcessBuilder("reg", "query", RUN_KEY, "/v", VALUE_NAME)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val code = process.waitFor()
        code == 0 && output.contains(VALUE_NAME)
    }.getOrDefault(false)

    /** 打开/关闭开机自启动；返回操作是否成功 */
    fun setEnabled(enabled: Boolean): Boolean = runCatching {
        val path = exePath() ?: return false
        val command = if (enabled) {
            listOf("reg", "add", RUN_KEY, "/v", VALUE_NAME, "/t", "REG_SZ", "/d", "\"$path\"", "/f")
        } else {
            listOf("reg", "delete", RUN_KEY, "/v", VALUE_NAME, "/f")
        }
        val process = ProcessBuilder(command).redirectErrorStream(true).start()
        process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor() == 0
    }.getOrDefault(false)

    /** 当前程序路径（用于界面提示） */
    fun currentExePath(): String? = exePath()
}
