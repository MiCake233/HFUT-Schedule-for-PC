package com.hfut.schedule.logic.util.storage.kv

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.xah.common.platform.Platform
import okio.Path.Companion.toPath
import java.io.File

/**
 * 跨平台创建 Preferences DataStore：
 * Android 端原来用 `Context.dataStore` 扩展，桌面端没有 Context，
 * 统一改成「应用数据目录下的文件」，两端行为一致。
 */
fun createPlatformDataStore(name: String): DataStore<Preferences> {
    val dir = File(Platform.dataDirPath(), "datastore").apply { mkdirs() }
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { File(dir, "$name.preferences_pb").absolutePath.toPath() }
    )
}
