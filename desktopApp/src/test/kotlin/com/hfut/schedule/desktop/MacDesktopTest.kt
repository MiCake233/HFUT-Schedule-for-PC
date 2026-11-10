package com.hfut.schedule.desktop

import com.hfut.schedule.desktop.platform.AesGcmSecret
import com.hfut.schedule.desktop.platform.DesktopOs
import com.hfut.schedule.desktop.platform.MacAutoStart
import com.hfut.schedule.desktop.platform.MacNotifications
import com.hfut.schedule.desktop.platform.MacSecrets
import com.hfut.schedule.desktop.platform.currentDesktopOs
import com.xah.common.platform.resolveDesktopDataDir
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * macOS 适配里不依赖真机的部分：数据目录、钥匙串主密钥解析、AES-GCM、登录项 plist。
 * 真正的 DMG 和 `security` / `launchctl` 需要在 Mac 上跑。
 */
class MacDesktopTest {

    @Test
    fun dataDirFollowsTheHostConvention() {
        assertEquals(
            File("C:/Users/ada/AppData/Roaming", "HFUT-Schedule"),
            resolveDesktopDataDir("Windows 11", "C:/Users/ada/AppData/Roaming", "C:/Users/ada")
        )
        assertEquals(
            File("/Users/ada/Library/Application Support/HFUT-Schedule"),
            resolveDesktopDataDir("Mac OS X", null, "/Users/ada")
        )
        assertEquals(
            File("/home/ada/.hfut-schedule"),
            resolveDesktopDataDir("Linux", null, "/home/ada")
        )
    }

    @Test
    fun dataDirOverrideWins() {
        assertEquals(
            File("/tmp/hfut"),
            resolveDesktopDataDir("Mac OS X", null, "/Users/ada", "/tmp/hfut")
        )
    }

    @Test
    fun osDetection() {
        assertEquals(DesktopOs.WINDOWS, currentDesktopOs("Windows 10"))
        assertEquals(DesktopOs.MACOS, currentDesktopOs("Mac OS X"))
        assertEquals(DesktopOs.OTHER, currentDesktopOs("Linux"))
    }

    @Test
    fun aesGcmRoundTripAndTamper() {
        val key = ByteArray(32) { it.toByte() }
        val encoded = AesGcmSecret.encrypt("tgc-value", key)
        assertFalse(encoded.contains("tgc-value"))
        assertEquals("tgc-value", AesGcmSecret.decrypt(encoded, key))
        val flipped = encoded.dropLast(1) + if (encoded.last() == 'A') "B" else "A"
        assertNull(AesGcmSecret.decrypt(flipped, key))
        assertNull(AesGcmSecret.decrypt(encoded, ByteArray(32) { 7 }))
    }

    @Test
    fun keychainPayloadMustBe32Bytes() {
        val key = java.util.Base64.getEncoder().encodeToString(ByteArray(32) { 1 })
        assertEquals(32, MacSecrets.decodeKey(key)!!.size)
        assertNull(MacSecrets.decodeKey("not-base64"))
        assertNull(MacSecrets.decodeKey(java.util.Base64.getEncoder().encodeToString(ByteArray(8))))
    }

    @Test
    fun launchAgentPlistNamesTheAppAndMinimizedArg() {
        val xml = MacAutoStart.plistXml("/Applications/HFUT-Schedule.app/Contents/MacOS/HFUT-Schedule", "--minimized")
        assertTrue(xml.contains("com.hfut.schedule.desktop"))
        assertTrue(xml.contains("/Applications/HFUT-Schedule.app/Contents/MacOS/HFUT-Schedule"))
        assertTrue(xml.contains("--minimized"))
        assertTrue(xml.contains("<key>RunAtLoad</key>"))
        val escaped = MacAutoStart.plistXml("/tmp/a&b<c>.app", "--minimized")
        assertTrue(escaped.contains("/tmp/a&amp;b&lt;c&gt;.app"))
        assertFalse(escaped.contains("/tmp/a&b"))
    }

    @Test
    fun notificationScriptEscapesQuotes() {
        assertEquals("say \\\"hi\\\\there\\\"", MacNotifications.escape("say \"hi\\there\""))
    }
}
