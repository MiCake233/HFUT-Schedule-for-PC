package com.xah.common.platform

/**
 * 全平台能力抽象。
 *
 * Android 侧走 android.* API；Windows 桌面（JVM）侧走 JDK API。
 * 这是整个 KMP 改造最底层的垫片，其余模块都依赖它，所以这里的 API 要尽量少而稳。
 */
expect object Platform {
    /** 平台标识，日志里用 */
    val name: String

    /** 是否 Debug 构建（Android 取 BuildConfig.DEBUG，桌面取系统属性 hfut.debug） */
    val isDebug: Boolean

    /**
     * 底层日志输出。
     * @param level 见 [LogLevels]
     */
    fun log(level: Int, tag: String, msg: String, throwable: Throwable?)

    /** 轻量提示。Android: Toast；桌面: 由 UI 注册的浮层提示，未注册时退化为控制台输出 */
    fun toast(msg: String)

    /** 应用数据目录（可写、随应用卸载/迁移保留） */
    fun dataDirPath(): String

    /** 应用缓存目录（可随时清理） */
    fun cacheDirPath(): String
}

/** 与 [Platform.log] 配合的日志级别常量 */
object LogLevels {
    const val VERBOSE = 0
    const val DEBUG = 1
    const val INFO = 2
    const val WARN = 3
    const val ERROR = 4
}
