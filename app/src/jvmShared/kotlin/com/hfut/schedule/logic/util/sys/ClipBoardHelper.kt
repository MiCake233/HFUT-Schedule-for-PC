package com.hfut.schedule.logic.util.sys

import com.xah.common.logic.util.LogUtil

/**
 * 剪贴板。
 * 逻辑（成功提示/异常兜底）放在共享代码里，平台相关只下沉到 [PlatformClipboard]。
 */
object ClipBoardHelper {
    private const val DEFAULT_TIPS = "已复制到剪切板"

    fun copy(str: String, tips: String? = DEFAULT_TIPS) {
        try {
            if (PlatformClipboard.set(str)) {
                tips?.let { showToast(it) }
            } else {
                showToast("复制到剪切板失败")
            }
        } catch (e: Exception) {
            LogUtil.error(e)
            showToast("复制到剪切板失败")
        }
    }

    fun paste(): String? {
        return try {
            PlatformClipboard.get()
        } catch (e: Exception) {
            LogUtil.error(e)
            showToast("获取剪切板内容失败")
            null
        }
    }
}

/** Android: ClipboardManager；桌面: AWT 剪贴板 */
expect object PlatformClipboard {
    fun set(text: String): Boolean
    fun get(): String?
}
