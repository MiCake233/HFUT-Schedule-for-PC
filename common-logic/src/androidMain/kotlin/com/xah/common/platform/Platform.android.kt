package com.xah.common.platform

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.File

/**
 * Android 侧需要在 Application.onCreate 里调用 [init] 注入 Context。
 */
object PlatformAndroid {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun contextOrNull(): Context? = appContext

    internal fun requireContext(): Context =
        appContext ?: error("PlatformAndroid.init(context) 尚未调用")

    /** 桌面端 Toast 占位：Android 用系统 Toast */
    internal fun showToast(msg: String) {
        val ctx = appContext ?: return
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
        }
    }
}

actual object Platform {
    actual val name: String = "android"

    actual val isDebug: Boolean = com.xah.common.logic.BuildConfig.DEBUG

    actual fun log(level: Int, tag: String, msg: String, throwable: Throwable?) {
        when (level) {
            LogLevels.VERBOSE -> Log.v(tag, msg)
            LogLevels.DEBUG -> Log.d(tag, msg)
            LogLevels.INFO -> Log.i(tag, msg)
            LogLevels.WARN -> Log.w(tag, msg)
            LogLevels.ERROR -> if (throwable != null) Log.e(tag, msg, throwable) else Log.e(tag, msg)
            else -> Log.d(tag, msg)
        }
    }

    actual fun toast(msg: String) = PlatformAndroid.showToast(msg)

    actual fun dataDirPath(): String = PlatformAndroid.requireContext().filesDir.absolutePath

    actual fun cacheDirPath(): String = PlatformAndroid.requireContext().cacheDir.absolutePath
}

/** 供 Android 侧使用：拼一个数据子目录（不存在则创建） */
fun PlatformAndroid.dataDirFile(): File = File(requireContext().filesDir.absolutePath).apply { mkdirs() }

fun PlatformAndroid.cacheDirFile(): File = File(requireContext().cacheDir.absolutePath).apply { mkdirs() }
