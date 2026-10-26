package com.xah.common.platform

import java.io.File

/**
 * Windows 桌面侧可注入的钩子：
 * - [toastHandler] 由桌面 UI 层注册，用 Compose 的浮层提示（Snackbar）呈现
 */
object PlatformDesktop {
    var toastHandler: ((String) -> Unit)? = null

    fun showToast(msg: String) {
        toastHandler?.invoke(msg) ?: println("[toast] $msg")
    }
}

actual object Platform {
    actual val name: String = "desktop-%s".format(System.getProperty("os.name", "unknown"))

    // 打包版默认不是调试构建：VERBOSE 级日志对普通用户没有价值，
    // 排查问题时用 -Dhfut.debug=true 启动即可拿回全量日志。
    actual val isDebug: Boolean =
        System.getProperty("hfut.debug")?.toBoolean() ?: false

    actual fun log(level: Int, tag: String, msg: String, throwable: Throwable?) {
        val prefix = when (level) {
            LogLevels.VERBOSE -> "V"
            LogLevels.DEBUG -> "D"
            LogLevels.INFO -> "I"
            LogLevels.WARN -> "W"
            LogLevels.ERROR -> "E"
            else -> "D"
        }
        val line = "$prefix/$tag: $msg"
        if (level >= LogLevels.WARN) System.err.println(line) else println(line)
        throwable?.printStackTrace()
    }

    actual fun toast(msg: String) = PlatformDesktop.showToast(msg)

    actual fun dataDirPath(): String {
        System.getProperty("hfut.dataDir")?.let { return ensureDir(it).absolutePath }
        val os = System.getProperty("os.name", "").lowercase()
        val base = if (os.contains("win")) {
            File(System.getenv("APPDATA") ?: System.getProperty("user.home"), "HFUT-Schedule")
        } else {
            File(System.getProperty("user.home"), ".hfut-schedule")
        }
        return ensureDir(base.absolutePath).absolutePath
    }

    actual fun cacheDirPath(): String =
        ensureDir(File(dataDirPath(), "cache").absolutePath).absolutePath

    private fun ensureDir(path: String): File = File(path).apply { mkdirs() }
}
