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
    val prefs: PlatformSharedPreferences by lazy { PlatformSharedPreferences(platformPrefsFile(PREFS)) }

    private val saved: PlatformSharedPreferences by lazy {
        PlatformSharedPreferences(platformPrefsFile("${AppCapabilities.packageName()}_default"))
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
