package com.hfut.schedule.desktop

import com.hfut.schedule.logic.platform.PlatformFeatures
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 查询中心在桌面端的取舍：相机、办事大厅 WebView、支付宝 / 商店深链要关掉；
 * 走网络接口的洗浴、电费、校园卡、洗衣保持可用。
 */
class DesktopQueryFeaturesTest {

    @Test
    fun cameraHallAndStoreLinksStayHidden() {
        assertFalse(PlatformFeatures.supports("scan_qr_code"))
        assertFalse(PlatformFeatures.supports("office_hall"))
        assertFalse(PlatformFeatures.supports("express"))
        assertFalse(PlatformFeatures.supportsFunc(2))
        assertFalse(PlatformFeatures.supportsFunc(23))
        assertFalse(PlatformFeatures.supportsFunc(43))
        assertFalse(PlatformFeatures.supportsFunc(49))
        assertFalse(PlatformFeatures.supportsAndroidAppLink())
        assertTrue(PlatformFeatures.DESKTOP_UNSUPPORTED.isNotEmpty())
    }

    @Test
    fun networkQueryEntriesStayAvailable() {
        assertTrue(PlatformFeatures.supportsFunc(1))
        assertTrue(PlatformFeatures.supportsFunc(3))
        assertTrue(PlatformFeatures.supportsFunc(4))
        assertTrue(PlatformFeatures.supportsFunc(12))
        assertTrue(PlatformFeatures.supportsFunc(25))
        assertTrue(PlatformFeatures.supportsFunc(40))
        assertTrue(PlatformFeatures.supports("guagua_login"))
        assertTrue(PlatformFeatures.supports("guagua_home"))
        assertTrue(PlatformFeatures.supports("school_card"))
        assertTrue(PlatformFeatures.supports(null))
    }
}
