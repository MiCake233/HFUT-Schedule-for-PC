package com.hfut.schedule.desktop

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
