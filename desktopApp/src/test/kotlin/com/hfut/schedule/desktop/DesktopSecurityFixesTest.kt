package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.other.AppInfo
import com.hfut.schedule.logic.util.other.isDevVersionName
import com.hfut.schedule.logic.util.other.isNormalEnvironment
import com.hfut.schedule.logic.util.sys.DesktopBrowseDecision
import com.hfut.schedule.logic.util.sys.PlatformHooks
import com.hfut.schedule.logic.util.sys.PlatformStarter
import com.hfut.schedule.logic.util.sys.decideDesktopBrowse
import com.hfut.schedule.logic.util.sys.desktopBrowseRefusal
import com.hfut.schedule.logic.util.sys.openDesktopWeb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 10.1.0 复核后修掉的几处：重新登录、浏览器不带凭据、上游 Dev 后缀不误报环境异常。
 */
class DesktopSecurityFixesTest {

    @Test
    fun restartToLoginUsesTheWindowHook() {
        val previous = PlatformHooks.onRestartToLogin
        var called = false
        try {
            PlatformHooks.onRestartToLogin = { called = true }
            PlatformStarter.restartToLogin()
            assertTrue(called)
        } finally {
            PlatformHooks.onRestartToLogin = previous
        }
    }

    @Test
    fun returnToDesktopLoginClearsTheLoggedInFlag() {
        val previous = DesktopWindowState.loggedIn
        try {
            DesktopWindowState.loggedIn = true
            returnToDesktopLogin()
            assertFalse(DesktopWindowState.loggedIn)
        } finally {
            DesktopWindowState.loggedIn = previous
        }
    }

    @Test
    fun cookieAndTokenUrlsAreNotOpened() {
        val payment = "http://121.251.19.62/charge-app/?name=pays&token=SECRET"
        val card = "http://121.251.19.62/plat/pay?synjones-auth=SECRET"
        val account = "http://jxglstu.hfut.edu.cn/eams5-student/my/account"
        val news = "https://news.hfut.edu.cn/info/1001.htm"

        assertEquals(DesktopBrowseDecision.CarriesSecret, decideDesktopBrowse(payment))
        assertEquals(DesktopBrowseDecision.CarriesSecret, decideDesktopBrowse(card))
        assertEquals(DesktopBrowseDecision.NeedsSession, decideDesktopBrowse(account, cookie = "SESSION=abc"))
        assertEquals(DesktopBrowseDecision.Allow, decideDesktopBrowse(news))
        assertEquals(DesktopBrowseDecision.Allow, decideDesktopBrowse(account))
        assertEquals(
            DesktopBrowseDecision.CarriesSecret,
            decideDesktopBrowse("https://user:secret@example.com/a"),
        )

        assertNull(desktopBrowseRefusal(DesktopBrowseDecision.Allow))
        val refusal = desktopBrowseRefusal(DesktopBrowseDecision.CarriesSecret)!!
        assertFalse(refusal.contains("SECRET"))
        assertFalse(refusal.contains("http"))
        assertFalse(desktopBrowseRefusal(DesktopBrowseDecision.NeedsSession)!!.contains("SESSION"))
        assertFalse(openDesktopWeb(payment))
        assertFalse(openDesktopWeb(account, cookie = "SESSION=abc"))
    }

    @Test
    fun upstreamDevSuffixDoesNotFlagAPackagedDesktopBuild() {
        assertTrue(isDevVersionName("4.21.4.2 Dev", informational = false))
        assertFalse(isDevVersionName("4.21.4.2 Dev", informational = true))
        assertFalse(isDevVersionName("4.21.4.2", informational = false))
        assertTrue(
            isNormalEnvironment(
                debug = false,
                signatureValid = true,
                dev = false,
                runningOnAvd = false,
                runningOnWsa = false,
                harmony = false,
            )
        )
        assertFalse(
            isNormalEnvironment(
                debug = false,
                signatureValid = true,
                dev = true,
                runningOnAvd = false,
                runningOnWsa = false,
                harmony = false,
            )
        )

        val name = AppInfo.versionName
        val informational = AppInfo.versionLabelIsInformational
        val debug = AppInfo.isDebug
        val signature = AppInfo.isSignatureValid
        val avd = AppInfo.isRunningOnAvd
        val wsa = AppInfo.isRunningOnWsa
        val harmony = AppInfo.isHarmony
        try {
            AppInfo.versionName = "4.21.4.2 Dev"
            AppInfo.isDebug = false
            AppInfo.isSignatureValid = true
            AppInfo.isRunningOnAvd = false
            AppInfo.isRunningOnWsa = false
            AppInfo.isHarmony = false
            AppInfo.versionLabelIsInformational = false
            assertTrue(AppInfo.isDev)
            assertFalse(AppInfo.isNormalEnv)
            AppInfo.versionLabelIsInformational = true
            assertFalse(AppInfo.isDev)
            assertTrue(AppInfo.isNormalEnv)
            assertEquals("4.21.4.2 Dev", AppInfo.getVersionName())
        } finally {
            AppInfo.versionName = name
            AppInfo.versionLabelIsInformational = informational
            AppInfo.isDebug = debug
            AppInfo.isSignatureValid = signature
            AppInfo.isRunningOnAvd = avd
            AppInfo.isRunningOnWsa = wsa
            AppInfo.isHarmony = harmony
        }
    }
}
