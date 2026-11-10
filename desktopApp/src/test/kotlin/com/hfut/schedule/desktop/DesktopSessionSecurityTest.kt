package com.hfut.schedule.desktop

import com.hfut.schedule.logic.network.interceptor.extractCasTgc
import com.hfut.schedule.logic.network.interceptor.extractCasTicket
import com.hfut.schedule.logic.network.interceptor.locationMatchesService
import com.hfut.schedule.logic.network.interceptor.setCookieValue
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.sys.escapeIcsText
import com.hfut.schedule.network.api.model.Constant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 登录跳转、Cookie 解析和日历导出里已经出过问题的边界。
 * 不联网。
 */
class DesktopSessionSecurityTest {

    @Test
    fun serviceMatchIsByHostAndPathNotSubstring() {
        val service = Constant.JXGLSTU_URL
        assertTrue(locationMatchesService(
            "http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login?ticket=ST-1",
            service
        ))
        assertFalse(locationMatchesService(
            "https://evil.example/steal?next=${service}",
            service
        ))
        assertFalse(locationMatchesService(
            "http://jxglstu.hfut.edu.cn.evil.example/eams5-student/login",
            service
        ))
        assertFalse(locationMatchesService(
            "http://user:pass@jxglstu.hfut.edu.cn/eams5-student/login",
            service
        ))
    }

    @Test
    fun sessionRestoreRefusesOffHostRedirects() {
        assertTrue(isTrustedSessionUrl("https://cas.hfut.edu.cn/cas/login?service=1"))
        assertTrue(isTrustedSessionUrl("http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login?ticket=ST-1"))
        assertFalse(isTrustedSessionUrl("https://evil.example/cas/login?ticket=ST-1"))
        assertFalse(isTrustedSessionUrl("https://cas.hfut.edu.cn.evil.example/cas/login"))
        assertFalse(isTrustedSessionUrl("javascript:alert(1)"))

        val cas = "https://cas.hfut.edu.cn/cas/login?service=x"
        assertNull(resolveTrusted(cas, "https://evil.example/collect?ticket=ST-1"))
        val allowed = resolveTrusted(
            cas,
            "http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login?ticket=ST-1"
        )
        assertEquals(
            "http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login?ticket=ST-1",
            allowed
        )
    }

    @Test
    fun tgcIsTakenByCookieNameNotByFirstHeader() {
        assertNull(setCookieValue("JSESSIONID=abc; Path=/", "SESSION"))
        assertEquals("abc", setCookieValue("SESSION=abc; Path=/", "SESSION"))
        assertEquals(
            "TGC=TGT-1",
            extractCasTgc(listOf("JSESSIONID=abc; Path=/", "TGC=TGT-1; Path=/cas; HttpOnly"))
        )
        assertNull(extractCasTgc(listOf("JSESSIONID=abc; Path=/")))
        assertNull(extractCasTicket(null))
        assertNull(extractCasTicket("null"))
        assertEquals(
            "ST-9",
            extractCasTicket("http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login?redirect=foo&ticket=ST-9&x=1")
        )
    }

    @Test
    fun logoutClearsLeftoverSessionKeys() {
        val keys = DesktopSession.credentialKeysClearedOnLogout
        assertTrue(keys.contains("stu"))
        assertTrue(keys.contains("LOGIN_FLAVORING"))
        assertTrue(keys.contains("webVpnKey"))
        assertTrue(keys.contains("TGC"))
        assertTrue(keys.contains("redirect"))
    }

    @Test
    fun icsEscapesCarriageReturnAndUsesCrlf() {
        val injected = "A\r\nSUMMARY:injected"
        assertEquals("A\\nSUMMARY:injected", escapeIcsText(injected))
        assertFalse(escapeIcsText(injected).contains("\r"))
        assertFalse(escapeIcsText(injected).contains("\n"))

        val home = File(System.getProperty("java.io.tmpdir"), "hfut-ics-home-" + System.nanoTime())
        File(home, "Downloads").mkdirs()
        val previous = System.getProperty("user.home")
        System.setProperty("user.home", home.absolutePath)
        try {
            runBlocking {
                addToCalendars(
                    startDate = "2026-03-02",
                    startTime = "08:00",
                    endDate = "2026-03-02",
                    endTime = "09:40",
                    place = "主楼 201",
                    title = injected,
                    remark = "line1\rline2",
                    activity = null
                )
            }
            val text = File(home, "Downloads/HFUT-Schedule.ics").readText()
            assertTrue(text.startsWith("BEGIN:VCALENDAR\r\n"))
            assertFalse(text.replace("\r\n", "").contains("\n"))
            val summaries = text.lines().filter { it.startsWith("SUMMARY:") }
            assertEquals(listOf("SUMMARY:A\\nSUMMARY:injected"), summaries)
        } finally {
            if (previous == null) System.clearProperty("user.home") else System.setProperty("user.home", previous)
            home.deleteRecursively()
        }
    }
}
