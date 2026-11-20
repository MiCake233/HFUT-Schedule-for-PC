package com.hfut.schedule.desktop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 更新检查的纯逻辑测试：版本比较、GitHub `releases/latest` 解析。
 *
 * 这些用例覆盖的都是「解析错了会导致下错包 / 不提示 / 提示错」的地方，
 * 不联网，不依赖 Windows。
 */
class DesktopUpdateTest {

    // ---- 版本比较 ----

    @Test
    fun newerPatchIsDetected() {
        assertTrue(DesktopUpdate.isNewer("10.0.1", "10.0.0"))
    }

    @Test
    fun sameVersionIsNotNewer() {
        assertFalse(DesktopUpdate.isNewer("10.0.0", "10.0.0"))
    }

    @Test
    fun olderVersionIsNotNewer() {
        assertFalse(DesktopUpdate.isNewer("9.9.9", "10.0.0"))
    }

    @Test
    fun numericCompareIsNotLexicographic() {
        // "10" 必须大于 "9"（字符串比较会判反）
        assertTrue(DesktopUpdate.isNewer("10.0.0", "9.0.0"))
        assertFalse(DesktopUpdate.isNewer("9.0.0", "10.0.0"))
    }

    @Test
    fun missingSegmentsDefaultToZero() {
        assertFalse(DesktopUpdate.isNewer("10", "10.0.0"))
        assertTrue(DesktopUpdate.isNewer("10.1", "10.0.9"))
    }

    @Test
    fun prereleaseSuffixOfSameVersionIsNotNewer() {
        assertFalse(DesktopUpdate.isNewer("10.0.0-rc1", "10.0.0"))
    }

    @Test
    fun garbageVersionsNeverUpgrade() {
        assertFalse(DesktopUpdate.isNewer("", "10.0.0"))
        assertFalse(DesktopUpdate.isNewer("latest", "10.0.0"))
        assertFalse(DesktopUpdate.isNewer("10.0.0", "abc"))
    }

    // ---- 响应解析 ----

    private val sample = """
        {
          "tag_name": "v10.0.1",
          "html_url": "https://github.com/MiCake233/HFUT-Schedule-for-PC/releases/tag/v10.0.1",
          "assets": [
            {
              "name": "HFUT-Schedule-10.0.1-portable.zip",
              "size": 83814345,
              "digest": "sha256:2eca25801468482c91556ee48c6cc3aa92dfa07551051509ad1794d5ce0d7ed3",
              "browser_download_url": "https://github.com/x/releases/download/v10.0.1/HFUT-Schedule-10.0.1-portable.zip",
              "uploader": { "login": "MiCake233", "html_url": "https://github.com/MiCake233" }
            },
            {
              "name": "HFUT-Schedule-10.0.1.msi",
              "size": 85166949,
              "digest": "sha256:741c2518d7362a118ef5a668efaa5ef27babf48b73f005f2dd354aa04368385c",
              "browser_download_url": "https://github.com/x/releases/download/v10.0.1/HFUT-Schedule-10.0.1.msi",
              "uploader": { "login": "MiCake233", "html_url": "https://github.com/MiCake233" }
            }
          ]
        }
    """.trimIndent()

    @Test
    fun parsesTagWithoutLeadingV() {
        val parsed = DesktopUpdate.parseLatest(sample)
        assertNotNull(parsed)
        assertEquals("10.0.1", parsed!!.latest)
    }

    @Test
    fun picksMsiAssetAndItsDigest() {
        val asset = DesktopUpdate.parseLatest(sample)!!.asset
        assertNotNull(asset)
        assertEquals("HFUT-Schedule-10.0.1.msi", asset!!.name)
        assertEquals("741c2518d7362a118ef5a668efaa5ef27babf48b73f005f2dd354aa04368385c", asset.sha256)
        assertEquals(85166949L, asset.size)
        assertTrue(asset.url.endsWith(".msi"))
    }

    @Test
    fun takesReleasePageUrlNotAssetUrl() {
        val parsed = DesktopUpdate.parseLatest(sample)!!
        assertTrue(parsed.pageUrl.contains("/releases/tag/"))
        assertFalse(parsed.pageUrl.endsWith(".msi"))
    }

    @Test
    fun releaseWithoutMsiYieldsNullAsset() {
        val body = """
            {"tag_name":"v2.0.0","html_url":"https://github.com/x/releases/tag/v2.0.0",
             "assets":[{"name":"source.zip","browser_download_url":"https://github.com/x/source.zip"}]}
        """.trimIndent()
        val parsed = DesktopUpdate.parseLatest(body)
        assertNotNull(parsed)
        assertNull(parsed!!.asset)
    }

    @Test
    fun missingDigestIsTolerated() {
        val body = """
            {"tag_name":"v2.0.0","html_url":"https://github.com/x","assets":[
              {"name":"HFUT-Schedule-2.0.0.msi","size":123,"browser_download_url":"https://github.com/x/a.msi"}]}
        """.trimIndent()
        val asset = DesktopUpdate.parseLatest(body)!!.asset
        assertNotNull(asset)
        assertNull(asset!!.sha256)
        assertEquals(123L, asset.size)
    }

    @Test
    fun missingTagIsRejected() {
        assertNull(DesktopUpdate.parseLatest("""{"html_url":"https://github.com/x"}"""))
    }

    @Test
    fun brokenJsonDoesNotThrow() {
        assertNull(DesktopUpdate.parseLatest("{\"tag_name\":"))
        assertNull(DesktopUpdate.parseLatest(""))
    }

    @Test
    fun updateUrlMustBeHttpsOnGithub() {
        assertTrue(DesktopUpdate.isTrustedUpdateUrl(
            "https://github.com/MiCake233/HFUT-Schedule-for-PC/releases/download/v10.0.1/a.msi"
        ))
        assertTrue(DesktopUpdate.isTrustedUpdateUrl("https://release-assets.githubusercontent.com/github-production-release-asset/a"))
        assertTrue(DesktopUpdate.isTrustedUpdateUrl("https://objects.githubusercontent.com/github-production-release-asset/a"))
        assertFalse(DesktopUpdate.isTrustedUpdateUrl("http://github.com/a/b/c.msi"))
        assertFalse(DesktopUpdate.isTrustedUpdateUrl("https://evil.example/a.msi"))
        assertFalse(DesktopUpdate.isTrustedUpdateUrl("https://github.com.evil.example/a.msi"))
        assertFalse(DesktopUpdate.isTrustedUpdateUrl("https://user:pass@github.com/a.msi"))
    }

    @Test
    fun picksDmgWhenAskedAndKeepsItsDigest() {
        val body = sample.replace(
            "\"name\": \"HFUT-Schedule-10.0.1.msi\"",
            "\"name\": \"HFUT-Schedule-10.0.1.dmg\""
        ).replace(
            "https://github.com/x/releases/download/v10.0.1/HFUT-Schedule-10.0.1.msi",
            "https://github.com/x/releases/download/v10.0.1/HFUT-Schedule-10.0.1.dmg"
        )
        val asset = DesktopUpdate.parseLatest(body, ".dmg")!!.asset
        assertNotNull(asset)
        assertEquals("HFUT-Schedule-10.0.1.dmg", asset!!.name)
        assertEquals("741c2518d7362a118ef5a668efaa5ef27babf48b73f005f2dd354aa04368385c", asset.sha256)
        assertTrue(asset.url.startsWith("https://"))
        assertTrue(asset.url.endsWith(".dmg"))
    }

    @Test
    fun dmgRequestIgnoresMsiAsset() {
        assertNull(DesktopUpdate.parseLatest(sample, ".dmg")!!.asset)
    }

    @Test
    fun nonMacHostsKeepMsiAsTheInstaller() {
        // 这台机器不是 macOS 时，默认必须仍是 .msi，避免 Windows 更新被改去下 DMG。
        val os = System.getProperty("os.name", "").lowercase()
        if (!os.contains("mac")) {
            assertEquals(".msi", com.hfut.schedule.desktop.platform.preferredInstallerSuffix())
            assertEquals("HFUT-Schedule-10.0.1.msi", DesktopUpdate.parseLatest(sample)!!.asset!!.name)
        }
    }

    @Test
    fun dmgWithoutKolyTrailerIsRejected() {
        val file = File.createTempFile("hfut-update", ".dmg")
        try {
            file.writeBytes(ByteArray(600))
            val rejected = DesktopUpdate.verifyLocalFile(file, "a".repeat(64), file.length())
            assertTrue(rejected is DesktopUpdate.Download.Error)
        } finally {
            file.delete()
        }
    }

    @Test
    fun dmgWithKolyTrailerAndMatchingHashIsAccepted() {
        val file = File.createTempFile("hfut-update", ".dmg")
        try {
            val bytes = ByteArray(512)
            bytes[0] = 'k'.code.toByte()
            bytes[1] = 'o'.code.toByte()
            bytes[2] = 'l'.code.toByte()
            bytes[3] = 'y'.code.toByte()
            file.writeBytes(bytes)
            val sha = java.security.MessageDigest.getInstance("SHA-256").digest(bytes)
                .joinToString("") { "%02x".format(it) }
            val accepted = DesktopUpdate.verifyLocalFile(file, sha, file.length())
            assertTrue(accepted is DesktopUpdate.Download.Ok)
        } finally {
            file.delete()
        }
    }

    @Test
    fun missingSha256RejectsOtherwiseValidMsi() {
        val file = File.createTempFile("hfut-update", ".msi")
        try {
            val bytes = ByteArray(64)
            bytes[0] = 0xD0.toByte()
            bytes[1] = 0xCF.toByte()
            bytes[2] = 0x11
            bytes[3] = 0xE0.toByte()
            file.writeBytes(bytes)
            val rejected = DesktopUpdate.verifyLocalFile(file, null, file.length())
            assertTrue(rejected is DesktopUpdate.Download.Error)
            val shortHash = DesktopUpdate.verifyLocalFile(file, "abcd", file.length())
            assertTrue(shortHash is DesktopUpdate.Download.Error)
        } finally {
            file.delete()
        }
    }
}
