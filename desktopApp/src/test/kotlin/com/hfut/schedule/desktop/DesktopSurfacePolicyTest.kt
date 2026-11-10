package com.hfut.schedule.desktop

import com.hfut.schedule.desktop.platform.DesktopOs
import com.hfut.schedule.desktop.platform.MacActivation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Hiding a window must drop it from the composition. Login state is a separate
 * flag and is not cleared by that decision.
 */
class DesktopSurfacePolicyTest {

    @Test
    fun hiddenMainIsNotComposed() {
        val surfaces = desktopSurfaces(mainRequested = false, widgetShown = false, fixShown = false)
        assertFalse(surfaces.main)
        assertFalse(surfaces.any)
        assertTrue(surfaces.macAccessory)
        assertFalse(surfaces.widgetAutoRefresh)
    }

    @Test
    fun minimizedStillShowsTheTodayPanelWhenItWasLeftOpen() {
        val surfaces = desktopSurfaces(mainRequested = false, widgetShown = true, fixShown = false)
        assertFalse(surfaces.main)
        assertTrue(surfaces.widget)
        assertTrue(surfaces.any)
        assertFalse(surfaces.macAccessory)
        assertTrue(surfaces.widgetAutoRefresh)
        assertTrue(shouldAutoRefreshTodayCourses(panelShown = true))
        assertFalse(shouldAutoRefreshTodayCourses(panelShown = false))
    }

    @Test
    fun visibleMainKeepsARegularApp() {
        val surfaces = desktopSurfaces(mainRequested = true, widgetShown = false, fixShown = false)
        assertTrue(surfaces.main)
        assertFalse(surfaces.macAccessory)
    }

    @Test
    fun fixWindowAloneIsEnoughToStayRegular() {
        val surfaces = desktopSurfaces(mainRequested = false, widgetShown = false, fixShown = true)
        assertTrue(surfaces.fix)
        assertFalse(surfaces.macAccessory)
    }

    @Test
    fun closeToTrayHidesAndQuitExits() {
        assertEquals(CloseAction.HideToTray, actionOnClose(closeToTray = true, alreadyExiting = false))
        assertEquals(CloseAction.Exit, actionOnClose(closeToTray = false, alreadyExiting = false))
        assertEquals(CloseAction.Exit, actionOnClose(closeToTray = true, alreadyExiting = true))
    }

    @Test
    fun keeperWindowStaysForTheWholeRunUntilQuit() {
        assertTrue(shouldKeepApplicationAlive(exiting = false))
        assertFalse(shouldKeepApplicationAlive(exiting = true))
    }

    @Test
    fun hidingDoesNotClearTheLoginFlag() {
        val previous = DesktopWindowState.loggedIn
        try {
            DesktopWindowState.loggedIn = true
            desktopSurfaces(mainRequested = false, widgetShown = false, fixShown = false)
            assertTrue(DesktopWindowState.loggedIn)
        } finally {
            DesktopWindowState.loggedIn = previous
        }
    }

    @Test
    fun activationPolicyIsNotAppliedOffMac() {
        assertFalse(MacActivation.apply(accessory = true, os = DesktopOs.WINDOWS))
        assertFalse(MacActivation.apply(accessory = false, os = DesktopOs.OTHER))
        assertEquals(1, MacActivation.policyCode(true))
        assertEquals(0, MacActivation.policyCode(false))
    }

    @Test
    fun nativeHideAfterTheWindowWasShownTearsItDownOnce() {
        val tracker = NativeWindowCloseTracker()
        assertFalse(tracker.onHidden())
        tracker.onShown()
        assertTrue(tracker.onHidden())
        assertFalse(tracker.onHidden())
        tracker.onShown()
        assertFalse(tracker.onHidden())
    }

    @Test
    fun heapReleaseIsRequestedWithoutTouchingTheUiThreadContract() {
        val before = BackgroundMemory.releaseRequests
        BackgroundMemory.releaseAfterHide()
        assertEquals(before + 1, BackgroundMemory.releaseRequests)
    }
}
