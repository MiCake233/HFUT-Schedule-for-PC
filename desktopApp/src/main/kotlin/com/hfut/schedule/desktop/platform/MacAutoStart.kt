package com.hfut.schedule.desktop.platform

import java.io.File

/**
 * macOS 登录时启动：写 `~/Library/LaunchAgents/com.hfut.schedule.desktop.plist`。
 *
 * `RunAtLoad` 让用户下次登录时由 launchd 拉起，参数 `--minimized` 与 Windows 启动项一致。
 * 本机如果有 `launchctl`，会马上 bootstrap / bootout；没有也不算失败，plist 仍在下次登录生效。
 */
internal object MacAutoStart {

    private const val LABEL = "com.hfut.schedule.desktop"
    const val START_ARG = "--minimized"

    fun plistFile(home: String = System.getProperty("user.home")): File =
        File(home, "Library/LaunchAgents/$LABEL.plist")

    fun executablePath(): String? {
        val packaged = System.getProperty("jpackage.app-path")
        if (!packaged.isNullOrBlank()) return packaged
        val cmd = runCatching { ProcessHandle.current().info().command().orElse(null) }.getOrNull()
        if (cmd.isNullOrBlank()) return null
        // IDE 里的 java 进程不能拿来做登录项
        return cmd.takeUnless { it.endsWith("java") || it.endsWith("java.exe") }
    }

    fun isEnabled(home: String = System.getProperty("user.home")): Boolean {
        val file = plistFile(home)
        return file.isFile && file.readText().contains(LABEL)
    }

    fun setEnabled(enabled: Boolean, home: String = System.getProperty("user.home")): Boolean {
        val file = plistFile(home)
        if (!enabled) {
            if (file.isFile) bootout(file)
            return !file.exists() || file.delete()
        }
        val exe = executablePath() ?: return false
        file.parentFile?.mkdirs()
        file.writeText(plistXml(exe, START_ARG))
        bootstrap(file)
        return file.isFile
    }

    internal fun plistXml(executable: String, argument: String): String = """
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
        <plist version="1.0">
        <dict>
          <key>Label</key>
          <string>$LABEL</string>
          <key>ProgramArguments</key>
          <array>
            <string>${xml(executable)}</string>
            <string>${xml(argument)}</string>
          </array>
          <key>RunAtLoad</key>
          <true/>
        </dict>
        </plist>
    """.trimIndent() + "\n"

    private fun xml(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    private fun bootstrap(file: File) {
        val uid = uid() ?: return
        runCatching {
            ProcessBuilder("launchctl", "bootstrap", "gui/$uid", file.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
        }
    }

    private fun bootout(file: File) {
        val uid = uid() ?: return
        runCatching {
            ProcessBuilder("launchctl", "bootout", "gui/$uid", file.absolutePath)
                .redirectErrorStream(true)
                .start()
                .waitFor()
        }
    }

    private fun uid(): String? = runCatching {
        ProcessBuilder("id", "-u").start().inputStream.bufferedReader().use { it.readText() }.trim()
    }.getOrNull()?.takeIf { it.isNotEmpty() }
}
