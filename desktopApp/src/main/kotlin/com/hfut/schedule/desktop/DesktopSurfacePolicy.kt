package com.hfut.schedule.desktop

/**
 * Which native windows exist.
 *
 * `Window(visible = false)` still keeps the Compose tree and the Skia / IOSurface
 * buffers. A hidden window is therefore not composed at all; showing it creates a
 * new window and reads the saved bounds and login flag.
 */
internal data class DesktopSurfaces(
    val main: Boolean,
    val widget: Boolean,
    val fix: Boolean,
) {
    val any: Boolean get() = main || widget || fix

    /** macOS dock icon is hidden only when nothing is on screen. The tray stays. */
    val macAccessory: Boolean get() = !any

    /** The 5-minute today-course refresh lives inside the widget composition. */
    val widgetAutoRefresh: Boolean get() = widget
}

internal fun desktopSurfaces(
    mainRequested: Boolean,
    widgetShown: Boolean,
    fixShown: Boolean,
): DesktopSurfaces = DesktopSurfaces(
    main = mainRequested,
    widget = widgetShown,
    fix = fixShown,
)

internal enum class CloseAction { HideToTray, Exit }

/** Close button: hide when the user asked for that, otherwise quit. */
internal fun actionOnClose(closeToTray: Boolean, alreadyExiting: Boolean): CloseAction =
    if (closeToTray && !alreadyExiting) CloseAction.HideToTray else CloseAction.Exit

/**
 * Compose 1.11 ends `application { }` when the last Window leaves the tree.
 * The schedule window stays torn down. A 1 dp hidden placeholder is composed only
 * in this case, so the tray and reminders survive. Quitting must not keep it.
 */
internal fun shouldKeepApplicationAlive(exiting: Boolean, anySurface: Boolean) =
    !exiting && !anySurface

/**
 * macOS can hide the NSWindow (red close button) without delivering Compose's
 * `onCloseRequest`. The first hide after the window has actually been shown
 * must tear the composition down; later hides from that teardown are ignored.
 */
internal class NativeWindowCloseTracker {
    private var shown: Boolean = false
    private var handled: Boolean = false

    fun onShown() {
        shown = true
    }

    fun onHidden(): Boolean {
        if (!shown || handled) return false
        handled = true
        return true
    }
}

/**
 * Ask the VM to return unused heap after a window has been disposed.
 * The graphics buffers are the large part; this only tidies the Java heap.
 * It does not run on the UI thread.
 */
internal object BackgroundMemory {
    var releaseRequests: Int = 0
        private set

    fun releaseAfterHide() {
        releaseRequests++
        val worker = Thread {
            runCatching { System.gc() }
        }
        worker.isDaemon = true
        worker.name = "hfut-idle-gc"
        worker.start()
    }
}
