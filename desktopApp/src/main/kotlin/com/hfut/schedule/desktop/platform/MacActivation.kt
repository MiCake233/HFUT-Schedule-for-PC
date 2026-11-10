package com.hfut.schedule.desktop.platform

/**
 * macOS activation policy.
 *
 * Accessory: no Dock icon and no Cmd-Tab entry, which is what we want while the
 * process is only a menu-bar extra. Regular: normal app, used whenever any
 * window is on screen. The menu-bar item and Notification Center do not depend
 * on this policy.
 *
 * The Apple EAWT class is absent on Windows and on this Linux build, so the
 * call is a no-op there.
 */
internal object MacActivation {

    fun apply(accessory: Boolean, os: DesktopOs = currentDesktopOs()): Boolean {
        if (os != DesktopOs.MACOS) return false
        return runCatching {
            val appClass = Class.forName("com.apple.eawt.Application")
            val app = appClass.getMethod("getApplication").invoke(null)
            val policyClass = Class.forName("com.apple.eawt.Application\$ActivationPolicy")
            val policyName = if (accessory) "ACCESSORY" else "REGULAR"
            val policy = policyClass.enumConstants.first { (it as Enum<*>).name == policyName }
            appClass.getMethod("setActivationPolicy", policyClass).invoke(app, policy)
            if (!accessory) {
                runCatching {
                    appClass.getMethod("requestForeground", java.lang.Boolean.TYPE).invoke(app, true)
                }
            }
            true
        }.getOrDefault(false)
    }
}
