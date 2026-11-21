package com.hfut.schedule.logic.util.sys

import com.xah.common.logic.util.LogUtil
import java.awt.Desktop
import java.net.URI

/**
 * 桌面端打开网页的决定。
 *
 * 没有能注入 Cookie 的内嵌浏览器。需要登录态的页面、以及地址本身带凭据的页面，
 * 都不交给系统浏览器，也不把地址写进提示、日志或剪贴板。
 */
enum class DesktopBrowseDecision {
    Allow,
    NeedsSession,
    CarriesSecret,
}

private val CREDENTIAL_QUERY_KEYS = setOf(
    "token",
    "synjones-auth",
    "access_token",
    "refresh_token",
    "password",
    "passwd",
    "api_key",
    "apikey",
    "secret",
)

fun decideDesktopBrowse(url: String, cookie: String? = null): DesktopBrowseDecision {
    if (!cookie.isNullOrBlank()) return DesktopBrowseDecision.NeedsSession
    if (urlCarriesCredential(url)) return DesktopBrowseDecision.CarriesSecret
    return DesktopBrowseDecision.Allow
}

/** 拒绝打开时给用户看的固定文案。允许打开时为 null，文案里不含地址。 */
fun desktopBrowseRefusal(decision: DesktopBrowseDecision): String? = when (decision) {
    DesktopBrowseDecision.Allow -> null
    DesktopBrowseDecision.NeedsSession -> "桌面端没有内嵌浏览器，无法带上登录状态打开此页面"
    DesktopBrowseDecision.CarriesSecret -> "此页面地址包含登录凭据，桌面端不会交给系统浏览器"
}

/**
 * 按 [decideDesktopBrowse] 打开系统浏览器。
 * 失败提示不包含地址。`announceFailure` 为 false 时由调用方自己说明（例如邮件）。
 */
fun openDesktopWeb(url: String, cookie: String? = null, announceFailure: Boolean = true): Boolean {
    val refusal = desktopBrowseRefusal(decideDesktopBrowse(url, cookie))
    if (refusal != null) {
        showToast(refusal)
        return false
    }
    val opened = try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
            true
        } else {
            false
        }
    } catch (e: Exception) {
        LogUtil.error(e)
        false
    }
    if (!opened && announceFailure) showToast("打开浏览器失败")
    return opened
}

internal fun urlCarriesCredential(url: String): Boolean {
    val uri = runCatching { URI(url.trim()) }.getOrNull()
    if (uri?.userInfo != null) return true
    val query = uri?.rawQuery ?: url.substringAfter('?', missingDelimiterValue = "").substringBefore('#')
    if (query.isEmpty()) return false
    return query.split('&').any { pair ->
        val name = pair.substringBefore('=').trim().lowercase()
        val value = pair.substringAfter('=', missingDelimiterValue = "")
        name in CREDENTIAL_QUERY_KEYS && value.isNotEmpty()
    }
}
