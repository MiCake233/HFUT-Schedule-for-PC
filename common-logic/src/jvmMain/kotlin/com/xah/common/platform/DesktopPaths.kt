package com.xah.common.platform

import java.io.File

/**
 * 桌面数据目录。
 *
 * - Windows：`%APPDATA%\HFUT-Schedule`
 * - macOS：`~/Library/Application Support/HFUT-Schedule`
 * - 其它（含 Linux 开发机）：`~/.hfut-schedule`，不是正式发行目标
 *
 * [override] 对应 `-Dhfut.dataDir=`，自检和测试用它把数据指到临时目录。
 */
fun resolveDesktopDataDir(
    osName: String,
    appData: String?,
    userHome: String,
    override: String? = null,
): File {
    if (!override.isNullOrBlank()) return File(override)
    val os = osName.lowercase()
    return when {
        os.contains("win") -> File(appData ?: userHome, "HFUT-Schedule")
        os.contains("mac") -> File(userHome, "Library/Application Support/HFUT-Schedule")
        else -> File(userHome, ".hfut-schedule")
    }
}
