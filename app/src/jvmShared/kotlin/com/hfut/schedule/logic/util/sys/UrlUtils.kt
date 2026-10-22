package com.hfut.schedule.logic.util.sys

/** 从 URL 里取出站点名，作为页面标题的默认值 */
fun getPureUrl(url: String): String {
    return if (url.startsWith("file://")) {
        "本地"
    } else {
        url.substringAfter("://").substringBefore("/")
    }
}

/**
 * 校验是不是一个可打开的网页地址。
 * 原实现用 android.util.Patterns.WEB_URL，这里换成平台无关的等价校验。
 */
fun isValidWebUrl(url: String, strict: Boolean = false): Boolean {
    val checkUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
        if (strict) return false
        "http://$url"
    } else url

    val withoutScheme = checkUrl.removePrefix("http://").removePrefix("https://")
    if (withoutScheme.isEmpty()) return false
    val host = withoutScheme.substringBefore("/").substringBefore("?").substringBefore("#")
    if (host.isEmpty()) return false
    if (host.contains(' ')) return false
    // 至少包含一个点，或者是 localhost/IP
    val hasDot = host.contains('.')
    val isLocal = host.equals("localhost", ignoreCase = true) || host.matches(Regex("""\d{1,3}(\.\d{1,3}){3}(:\d+)?"""))
    return (hasDot || isLocal) && !host.startsWith(".") && !host.endsWith(".")
}
