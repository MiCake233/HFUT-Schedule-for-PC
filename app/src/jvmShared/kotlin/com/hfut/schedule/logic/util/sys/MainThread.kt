package com.hfut.schedule.logic.util.sys
import com.hfut.schedule.logic.util.sys.runOnMain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 主线程投递工具。
 *
 * 原代码大量使用 `runOnMain { }`（Android 专属），
 * 桌面端没有 Looper，这里用协程主调度器等价实现（两端通用）。
 */
private val mainScope: CoroutineScope by lazy {
    CoroutineScope(SupervisorJob() + Dispatchers.Main)
}

fun runOnMain(block: () -> Unit): Job = mainScope.launch { block() }

fun postDelayed(delayMillis: Long, block: () -> Unit): Job = mainScope.launch {
    delay(delayMillis)
    block()
}
