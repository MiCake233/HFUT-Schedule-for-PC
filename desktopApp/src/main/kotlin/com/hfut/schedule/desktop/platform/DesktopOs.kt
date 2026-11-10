package com.hfut.schedule.desktop.platform

/**
 * 桌面端只有一个 JVM 目标，Windows 和 macOS 不能拆成两套 `expect/actual` 源集。
 * 打包时 jpackage 按宿主系统出 MSI 或 DMG；运行时用这一层选择路径、自启动、钥匙串和安装包格式。
 */
enum class DesktopOs {
    WINDOWS,
    MACOS,
    OTHER,
}

fun currentDesktopOs(osName: String = System.getProperty("os.name", "")): DesktopOs {
    val os = osName.lowercase()
    return when {
        os.contains("win") -> DesktopOs.WINDOWS
        os.contains("mac") -> DesktopOs.MACOS
        else -> DesktopOs.OTHER
    }
}

/** 正式发行的安装包后缀。Linux 开发机沿用 `.msi`，和现有单测、Windows 发行保持一致。 */
fun preferredInstallerSuffix(os: DesktopOs = currentDesktopOs()): String = when (os) {
    DesktopOs.MACOS -> ".dmg"
    else -> ".msi"
}

fun trayLabel(os: DesktopOs = currentDesktopOs()): String = when (os) {
    DesktopOs.MACOS -> "菜单栏"
    else -> "托盘"
}
