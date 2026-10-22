package com.xah.common.ui.res

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString as composeGetString

/**
 * 非组合环境下的同步取字符串。
 * Compose Resources 只提供 suspend 的 getString，而原 Android 代码里存在大量
 * context.getString(...) 形式的同步调用；这里做一层带缓存的桥接，
 * 首次访问读一次资源文件，之后命中内存缓存。
 */
private val syncCache = HashMap<Int, String>()

fun stringOf(id: Int): String = syncCache.getOrPut(id) {
    runBlocking { composeGetString(Strings.resourceOf(id)) }
}

fun stringOf(id: Int, vararg args: Any): String =
    runBlocking { composeGetString(Strings.resourceOf(id), *args) }
