package com.xah.common.ui.res

/**
 * 按资源名查 Drawables 注册表里的 id（找不到返回 -1）。
 *
 * 原本 Android 端用 `context.resources.getIdentifier("qweather100", "drawable", pkg)`
 * 这类反射方式按名字取图（天气图标 400 多张），桌面端没有 Resources，
 * 这里改成查迁移后的注册表，两端共用。
 */
fun drawableIdOf(name: String): Int {
    for (i in 0 until Drawables.size) {
        if (Drawables.nameOf(i) == name) return i
    }
    return -1
}

fun drawableIdOrNull(name: String): Int? = drawableIdOf(name).takeIf { it >= 0 }
