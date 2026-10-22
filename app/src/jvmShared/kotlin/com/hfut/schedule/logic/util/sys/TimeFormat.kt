package com.hfut.schedule.logic.util.sys
import com.hfut.schedule.logic.util.sys.parseTimeItem

/**
 * 两位数补零（时间/日期格式化用）。
 * 原先定义在 FocusItems.kt（Android 专属文件）里，桌面端复用，抽到共享源集。
 */
fun parseTimeItem(item: Int): String = if (item < 10) "0$item" else item.toString()
