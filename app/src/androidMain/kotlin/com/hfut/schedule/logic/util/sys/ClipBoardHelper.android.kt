package com.hfut.schedule.logic.util.sys

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.hfut.schedule.application.MyApplication

actual object PlatformClipboard {
    private fun manager(): ClipboardManager? =
        MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    actual fun set(text: String): Boolean = try {
        manager()?.setPrimaryClip(ClipData.newPlainText(null, text))
        true
    } catch (e: Exception) {
        false
    }

    actual fun get(): String? = try {
        val clipboard = manager()
        if (clipboard != null && clipboard.hasPrimaryClip() && clipboard.primaryClip!!.itemCount > 0) {
            clipboard.primaryClip!!.getItemAt(0).text?.toString()
        } else null
    } catch (e: Exception) {
        null
    }
}
