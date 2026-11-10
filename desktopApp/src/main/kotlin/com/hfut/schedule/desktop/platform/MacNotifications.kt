package com.hfut.schedule.desktop.platform

/**
 * macOS 通知中心。
 *
 * Compose Desktop 的托盘气泡在未签名、未打包的进程上经常不出现。
 * `osascript` 的 `display notification` 走系统通知中心，菜单栏应用和 `java` 直接运行都能用。
 */
internal object MacNotifications {

    fun display(title: String, message: String): Boolean {
        if (currentDesktopOs() != DesktopOs.MACOS) return false
        val script = "display notification \"${escape(message)}\" with title \"${escape(title)}\""
        return runCatching {
            ProcessBuilder("osascript", "-e", script)
                .redirectErrorStream(true)
                .start()
                .waitFor() == 0
        }.getOrDefault(false)
    }

    /** 供单测检查引号和反斜杠不会把脚本截断。 */
    internal fun escape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}
