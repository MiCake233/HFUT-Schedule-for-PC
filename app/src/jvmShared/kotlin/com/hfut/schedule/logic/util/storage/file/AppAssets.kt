package com.hfut.schedule.logic.util.storage.file

/**
 * 读取随应用打包的静态资源（原来只有 Android 的 assets 有）。
 * Android 走 AssetManager，桌面端走 classpath（资源放在 app/src/jvmMain/resources）。
 */
expect fun readAppAsset(name: String): String?
