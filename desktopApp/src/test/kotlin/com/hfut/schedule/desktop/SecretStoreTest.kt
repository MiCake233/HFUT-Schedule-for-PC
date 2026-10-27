package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.storage.kv.PlatformSharedPreferences
import com.hfut.schedule.logic.util.storage.kv.SecretStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.Base64

/**
 * 凭据加密的分层测试。
 *
 * 这里用一个假加密器（可逆 XOR + base64）代替 DPAPI，所以能跑在任何机器上；
 * 真正的 DPAPI 由 `--verify-secret` 在 Windows 上端到端验证。
 */
class SecretStoreTest {

    private lateinit var dir: File

    /** 假的加密器：只要求「可逆」和「明文不出现」，用来验证挂钩逻辑 */
    private val fakeProtector: (String) -> String = { plain ->
        Base64.getEncoder().encodeToString(plain.toByteArray(Charsets.UTF_8).map { (it.toInt() xor 0x5A).toByte() }.toByteArray())
    }
    private val fakeUnprotector: (String) -> String = { encoded ->
        val bytes = Base64.getDecoder().decode(encoded).map { (it.toInt() xor 0x5A).toByte() }.toByteArray()
        String(bytes, Charsets.UTF_8)
    }

    @Before
    fun setUp() {
        dir = File(System.getProperty("java.io.tmpdir"), "hfut-secretstore-test-" + System.nanoTime())
        dir.mkdirs()
        SecretStore.protector = fakeProtector
        SecretStore.unprotector = fakeUnprotector
    }

    @After
    fun tearDown() {
        SecretStore.protector = null
        SecretStore.unprotector = null
        dir.deleteRecursively()
    }

    @Test
    fun sensitiveKeysAreClassified() {
        assertTrue(SecretStore.isSensitive("TGC"))
        assertTrue(SecretStore.isSensitive("redirect"))
        assertTrue(SecretStore.isSensitive("xwx_password"))
        assertTrue(SecretStore.isSensitive("webvpn_cookie"))
        assertFalse(SecretStore.isSensitive("Username"))
        assertFalse(SecretStore.isSensitive("color_mode"))
    }

    @Test
    fun nonSensitiveValueIsStoredAsIs() {
        assertEquals("2026212855", SecretStore.encode("Username", "2026212855"))
    }

    @Test
    fun sensitiveValueIsEncoded() {
        val encoded = SecretStore.encode("TGC", "TGT-123")
        assertNotNullEncoded(encoded!!)
        assertFalse(encoded.contains("TGT-123"))
        assertEquals("TGT-123", SecretStore.decode("TGC", encoded))
    }

    @Test
    fun alreadyEncodedValueIsNotDoubleEncoded() {
        val once = SecretStore.encode("TGC", "TGT-123")!!
        assertEquals(once, SecretStore.encode("TGC", once))
    }

    @Test
    fun legacyPlaintextIsReadable() {
        // 老版本写在磁盘上的明文必须还能读，否则升级后等于掉登录
        assertEquals("TGT-legacy", SecretStore.decode("TGC", "TGT-legacy"))
    }

    @Test
    fun withoutProtectorValuesStayPlaintext() {
        SecretStore.protector = null
        SecretStore.unprotector = null
        assertEquals("TGT-123", SecretStore.encode("TGC", "TGT-123"))
        assertEquals("TGT-123", SecretStore.decode("TGC", "TGT-123"))
    }

    @Test
    fun undecryptableValueBecomesNull() {
        // 换了 Windows 用户 / 换了机器：密文解不开，按「没有凭据」处理，而不是抛异常
        val encoded = SecretStore.encode("TGC", "TGT-123")!!
        SecretStore.unprotector = { throw IllegalStateException("wrong user") }
        assertNull(SecretStore.decode("TGC", encoded))
    }

    @Test
    fun prefsRoundTripThroughDisk() {
        val file = File(dir, "prefs.properties")
        val store = PlatformSharedPreferences(file)
        store.edit {
            putString("TGC", "TGT-secret-value")
            putString("Username", "2026212855")
        }

        val onDisk = file.readText()
        assertFalse("磁盘上不能出现明文凭据", onDisk.contains("TGT-secret-value"))
        // Properties.store 会把 ':' 转义成 '\:'，所以只断言标记前缀本身
        assertTrue("磁盘上应出现密文标记", onDisk.contains("dpapi1"))
        assertTrue("普通设置仍应可读", onDisk.contains("2026212855"))

        assertEquals("TGT-secret-value", store.getString("TGC", ""))
        assertEquals("2026212855", store.getString("Username", ""))
    }

    @Test
    fun prefsSurviveReloadFromDisk() {
        val file = File(dir, "reload.properties")
        PlatformSharedPreferences(file).edit { putString("redirect", "SESSION=abc") }
        // 模拟下次启动：新实例读同一个文件
        val reloaded = PlatformSharedPreferences(file)
        assertEquals("SESSION=abc", reloaded.getString("redirect", ""))
    }

    @Test
    fun nonStringTypesAreUntouched() {
        val file = File(dir, "types.properties")
        val store = PlatformSharedPreferences(file)
        store.edit {
            putInt("TGC", 42)          // 同名但非字符串，不应被当作凭据处理
            putBoolean("flag", true)
        }
        assertEquals(42, store.getInt("TGC", 0))
        assertTrue(store.getBoolean("flag", false))
    }

    private fun assertNotNullEncoded(value: String) {
        assertTrue("密文应带标记", SecretStore.isEncoded(value))
    }

    private fun assertNotNull(value: Any?) = org.junit.Assert.assertNotNull(value)
}
