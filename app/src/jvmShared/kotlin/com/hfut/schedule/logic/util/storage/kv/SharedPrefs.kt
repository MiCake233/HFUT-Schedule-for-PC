package com.hfut.schedule.logic.util.storage.kv

import com.hfut.schedule.logic.platform.AppCapabilities

// 特别想彻底重构这里，当时键值乱飞，已经难以重构了
/**
 * 除非很重要的值，否则不需要做值迁移。
 *
 * 跨平台改造：底层由 Android SharedPreferences 换成 [PlatformSharedPreferences]，
 * 对外 API 保持原样，桌面端同样可用。
 */
@Deprecated("为KMP适配计划的开始做铺垫，将彻底弃用SP，此类会被删除，请改用`DataStoreManager`存储键值对")
object SharedPrefs {
    private val PREFS = "${AppCapabilities.packageName()}_preferences"

    @Deprecated("为KMP适配计划的开始做铺垫垫，即将去除")
    const val LIBRARY_TOKEN = "LibraryToken"

    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    const val SECOND_CLASS_TOKEN = "second_class_token"

    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    val prefs: PlatformSharedPreferences by lazy {
        PlatformSharedPreferences(platformPrefsFile(PREFS)).also { migrateLegacyStore(it) }
    }

    /**
     * `saveString` 写入的 store。
     *
     * **必须和 [prefs] 是同一个文件**：Android 侧原来是
     * `PreferenceManager.getDefaultSharedPreferences(context)`，它返回的文件名恰好就是
     * `<packageName>_preferences`，与 [prefs] 完全同一个 SharedPreferences。
     *
     * 移植到桌面端时如果按字面意思另建一个 `_default` 文件，就会出现
     * 「AesKeyInterceptor 把 LOGIN_FLAVORING 存进 A 文件、登录页从 B 文件读」的情况，
     * 结果 CAS 登录拿不到 cookie → `CryptoUtil.encryptAES` 返回 null → 点击登录
     * 只转圈、不发请求、也不报错。这里显式对齐回同一个文件。
     */
    private val saved: PlatformSharedPreferences by lazy { prefs }

    /**
     * 老版本（`_default.properties`）里已经写过的键，一次性搬进正确的 store。
     *
     * 注意：必须通过参数 [target] 传入目标 store，**不能**在里面再读 `prefs`——
     * 本函数是在 `prefs` 的 lazy 初始化里被调用的，重入 `prefs` 会递归触发初始化。
     */
    private fun migrateLegacyStore(target: PlatformSharedPreferences) {
        runCatching {
            val legacy = platformPrefsFile("${AppCapabilities.packageName()}_default")
            if (!legacy.exists()) return@runCatching
            val entries = PlatformSharedPreferences(legacy).all()
            if (entries.isEmpty()) return@runCatching
            target.importMissing(entries)
        }
    }

    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    fun saveString(title: String, info: String?) {
        if (saved.getString(title, "") != info) { saved.edit { putString(title, info) } }
    }

    @Deprecated("为KMP适配计划的开始做铺垫，彻底弃用SP")
    fun saveLong(title: String, info: Long) {
        if (saved.getLong(title, 0L) != info) { saved.edit { putLong(title, info) } }
    }
}
