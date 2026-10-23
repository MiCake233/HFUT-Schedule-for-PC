package com.hfut.schedule.logic.util.storage.file

actual fun readAppAsset(name: String): String? = runCatching {
    val stream = Thread.currentThread().contextClassLoader?.getResourceAsStream(name)
        ?: object {}.javaClass.classLoader?.getResourceAsStream(name)
        ?: ClassLoader.getSystemResourceAsStream(name)
    stream?.bufferedReader()?.use { it.readText() }
}.getOrNull()
