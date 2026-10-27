package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.storage.kv.SecretStore
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import java.util.Base64

/**
 * 用 Windows DPAPI 加密本机保存的登录凭据。
 *
 * 之前 TGC / SESSION / 各种 token 是明文躺在 `%APPDATA%\HFUT-Schedule\prefs\*.properties` 里的，
 * 任何以当前用户身份运行的程序、同步盘、备份工具都能直接读到——等于拿到账号的会话。
 *
 * DPAPI（`CryptProtectData`）把密钥交给 Windows 凭据管理器，密文只有**同一个 Windows 用户**
 * 才能解开：文件被拷到别的账号 / 别的机器就只能得到一堆解不开的字节，程序会当作「未登录」。
 *
 * 非 Windows 平台不安装，[SecretStore] 自动退化为明文（与旧行为一致）。
 */
internal object Dpapi {

    private val isWindows: Boolean =
        System.getProperty("os.name", "").lowercase().contains("win")

    /** 安装加解密实现；成功返回 true */
    fun install(): Boolean {
        if (!isWindows) return false
        return runCatching {
            // 先验证一次往返，避免把「装上了但永远解不开」的状态留给用户
            val probe = "dpapi-self-test-" + System.nanoTime()
            val enc = protectRaw(probe) ?: return false
            if (unprotectRaw(enc) != probe) return false

            SecretStore.protector = { plain -> runCatching { protectRaw(plain) }.getOrNull() }
            SecretStore.unprotector = { encoded -> runCatching { unprotectRaw(encoded) }.getOrNull() }
            true
        }.getOrDefault(false)
    }

    private fun protectRaw(plain: String): String? {
        val protected = com.sun.jna.platform.win32.Crypt32Util.cryptProtectData(plain.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(protected)
    }

    private fun unprotectRaw(encoded: String): String? {
        val raw = Base64.getDecoder().decode(encoded)
        val plain = com.sun.jna.platform.win32.Crypt32Util.cryptUnprotectData(raw)
        return String(plain, Charsets.UTF_8)
    }

    /**
     * 把已经存在的明文凭据就地升级为密文。
     *
     * 读出来的值是明文（没有密文标记）→ 原样写回，写入路径会自动加密。
     * 覆盖两处存储：`SharedPrefs`（TGC / SESSION / token）与 `DataStore`（密码 / JWT / cookie）。
     * @return 升级了多少个键
     */
    fun migratePlaintextSecrets(): Int {
        if (SecretStore.protector == null) return 0
        var migrated = 0
        val prefs = SharedPrefs.prefs
        SecretStore.SENSITIVE_KEYS.forEach { key ->
            val value = runCatching { prefs.getString(key, "") }.getOrNull().orEmpty()
            if (value.isEmpty() || SecretStore.isEncoded(value)) return@forEach
            runCatching { prefs.edit { putString(key, value) } }
                .onSuccess { migrated++ }
        }
        // DataStore 里的密码 / JWT / cookie 同样处理（Android 上没装加密器时是空操作）
        migrated += runCatching {
            kotlinx.coroutines.runBlocking {
                com.hfut.schedule.logic.util.storage.kv.DataStoreManager.migrateSensitiveValues()
            }
        }.getOrDefault(0)
        return migrated
    }

    /** 供自检使用：加密 / 解密往返 */
    fun roundTrip(plain: String): Boolean {
        val enc = protectRaw(plain) ?: return false
        if (enc.contains(plain)) return false
        return unprotectRaw(enc) == plain
    }

    /** 供自检使用：一段被篡改的密文必须解不开（而不是抛异常把调用方带崩） */
    fun rejectsTampered(encoded: String): Boolean =
        runCatching { unprotectRaw(encoded) }.getOrNull() == null

    fun isAvailable(): Boolean = isWindows && runCatching { roundTrip("probe") }.getOrDefault(false)
}
