package com.hfut.schedule.logic.util.storage.file

import com.hfut.schedule.application.MyApplication

actual fun readAppAsset(name: String): String? = runCatching {
    MyApplication.context.assets.open(name).bufferedReader().use { it.readText() }
}.getOrNull()
