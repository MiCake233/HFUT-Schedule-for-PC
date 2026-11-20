package com.hfut.schedule.desktop.platform

import com.sun.jna.Function
import com.sun.jna.NativeLibrary
import com.sun.jna.NativeLong
import com.xah.common.logic.util.LogUtil
import java.awt.EventQueue

/**
 * macOS Dock / Cmd-Tab policy.
 *
 * `com.apple.eawt.Application.setActivationPolicy` is a no-op on current
 * macOS + Temurin once the process has started as a foreground app, which is
 * why `--minimized` still left a Dock icon. This calls AppKit directly:
 *
 * `NSApplicationActivationPolicyRegular` = 0, `Accessory` = 1.
 *
 * Accessory removes the Dock icon and the Cmd-Tab entry. The menu-bar item
 * and Notification Center stay. Showing any window switches back to regular
 * and activates the app. Windows never calls this.
 */
internal object MacActivation {

    /** AppKit `NSApplicationActivationPolicy`. */
    internal fun policyCode(accessory: Boolean): Int = if (accessory) 1 else 0

    fun apply(accessory: Boolean, os: DesktopOs = currentDesktopOs()): Boolean {
        if (os != DesktopOs.MACOS) return false
        return runCatching {
            if (EventQueue.isDispatchThread()) {
                setPolicyOnAppKit(accessory)
            } else {
                var ok = false
                EventQueue.invokeAndWait { ok = setPolicyOnAppKit(accessory) }
                if (!ok) error("setActivationPolicy failed")
            }
            true
        }.onFailure { LogUtil.error(it, "macOS activation policy") }
            .isSuccess
    }

    private fun setPolicyOnAppKit(accessory: Boolean): Boolean {
        val objc = NativeLibrary.getInstance("objc")
        val getClass = objc.getFunction("objc_getClass")
        val register = objc.getFunction("sel_registerName")
        val msg = objc.getFunction("objc_msgSend")
        val appClass = getClass.invokePointer(arrayOf<Any>("NSApplication"))
            ?: error("NSApplication class missing")
        val shared = register.invokePointer(arrayOf<Any>("sharedApplication"))
            ?: error("sharedApplication selector missing")
        val app = msg.invokePointer(arrayOf(appClass, shared))
            ?: error("NSApp missing")
        val setPolicy = register.invokePointer(arrayOf<Any>("setActivationPolicy:"))
            ?: error("setActivationPolicy: selector missing")
        // BOOL return. The argument is NSInteger (pointer-sized); 0 regular, 1 accessory.
        msg.invokeInt(arrayOf(app, setPolicy, NativeLong(policyCode(accessory).toLong())))
        if (!accessory) {
            val activate = register.invokePointer(arrayOf<Any>("activateIgnoringOtherApps:"))
            if (activate != null) {
                msg.invokeInt(arrayOf(app, activate, 1.toByte()))
            }
        }
        return true
    }
}
