package com.hfut.schedule.logic.util.storage.kv

import com.xah.common.platform.Platform
import java.io.File
import java.util.Properties

/**
 * 极简的跨平台键值存储，语义对齐 Android SharedPreferences 里本项目实际用到的那一小撮 API。
 *
 * 为什么不用 DataStore：项目里这些键是历史遗留的零散读写（150+ 处 getString），
 * 作者本人已将其标为 @Deprecated 准备迁移到 DataStoreManager，这里只做「搬到桌面端也能跑」的最小实现，
 * 类型用前缀区分（s| / b| / i| / l| / f|），落盘为 properties 文件。
 */
class PlatformSharedPreferences(private val file: File) {

    private val props = Properties().apply {
        runCatching { if (file.exists()) file.inputStream().use { load(it) } }
    }
    private val lock = Any()

    private fun read(key: String, type: Char): String? = synchronized(lock) { props.getProperty("$type|$key") }

    fun getString(key: String, def: String? = null): String? = read(key, 's') ?: def
    fun getBoolean(key: String, def: Boolean = false): Boolean = read(key, 'b')?.toBooleanStrictOrNull() ?: def
    fun getInt(key: String, def: Int = 0): Int = read(key, 'i')?.toIntOrNull() ?: def
    fun getLong(key: String, def: Long = 0L): Long = read(key, 'l')?.toLongOrNull() ?: def
    fun getFloat(key: String, def: Float = 0f): Float = read(key, 'f')?.toFloatOrNull() ?: def

    fun contains(key: String): Boolean = TYPES.any { synchronized(lock) { props.containsKey("$it|$key") } }

    fun all(): Map<String, String> = synchronized(lock) {
        props.stringPropertyNames().associateWith { props.getProperty(it) }
    }

    fun edit(block: Editor.() -> Unit) {
        val editor = Editor()
        editor.block()
        editor.commit()
    }

    /** 把外部 store 里已有的键值（形如 `s|KEY`）补进本 store，已存在的键不覆盖 */
    internal fun importMissing(entries: Map<String, String>) {
        if (entries.isEmpty()) return
        val missing = synchronized(lock) { entries.filterKeys { !props.containsKey(it) } }
        if (missing.isEmpty()) return
        val editor = Editor()
        editor.putRaw(missing)
        editor.commit()
    }

    inner class Editor {
        private val puts = mutableMapOf<String, String>()
        private val removes = mutableListOf<String>()

        fun putString(key: String, value: String?) = apply {
            if (value == null) removes.add("s|$key") else puts["s|$key"] = value
        }

        fun putBoolean(key: String, value: Boolean) = apply { puts["b|$key"] = value.toString() }
        fun putInt(key: String, value: Int) = apply { puts["i|$key"] = value.toString() }
        fun putLong(key: String, value: Long) = apply { puts["l|$key"] = value.toString() }
        fun putFloat(key: String, value: Float) = apply { puts["f|$key"] = value.toString() }

        fun remove(key: String) = apply { TYPES.forEach { removes.add("$it|$key") } }

        /** 直接写入已经带类型前缀的键（用于旧数据迁移） */
        internal fun putRaw(values: Map<String, String>) = apply { puts.putAll(values) }

        internal fun commit() {
            synchronized(lock) {
                removes.forEach { props.remove(it) }
                puts.forEach { (k, v) -> props.setProperty(k, v) }
                runCatching {
                    file.parentFile?.mkdirs()
                    file.outputStream().use { props.store(it, null) }
                }
            }
        }
    }

    private companion object {
        val TYPES = listOf('s', 'b', 'i', 'l', 'f')
    }
}

internal fun platformPrefsFile(name: String): File =
    File(File(Platform.dataDirPath(), "prefs").apply { mkdirs() }, "$name.properties")
