package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.Constant
import com.xah.common.logic.util.LogUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URI

private const val MAX_OAUTH_HOPS = 5

/**
 * CAS OAuth 跳转。
 *
 * 客户端已关闭自动重定向。这里自己跟着 Location 走，并用 [Request.follow]
 * 在换主机时丢掉 Cookie。信息门户和慧新易校共用这一条链路，两条成功跳转都可能带
 * `code=`，必须按主机分开，不能写进同一个 [GoToInterceptorState.toOneCode]。
 */
class GotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response = chain.proceed(request)
        var hops = 0
        while (response.isRedirect && hops < MAX_OAUTH_HOPS) {
            val raw = response.header("Location") ?: return response
            recordOauthRedirect(absoluteLocation(request, raw))
            val next = request.follow(raw)
            response.body?.close()
            request = next
            response = chain.proceed(request)
            hops++
        }
        return response
    }
}

/** 信息门户的授权码，或慧新易校的 synjones-auth。两者不会同时成立。 */
sealed class OauthRedirect {
    /** 信息门户换 token 要用整段 Location，不能只留 code。 */
    data class OneCode(val location: String) : OauthRedirect()
    data class HuiXinAuth(val token: String) : OauthRedirect()
}

/**
 * 只认落在对应服务上的跳转。
 *
 * `headers("Location").toString()` 是带方括号的列表，synjones-auth 在末尾时会把 `]` 收进票据。
 * 这里解析的是 Location 头本身的查询参数。慧新易校成功地址是 `redirect_uri&code=...`，
 * 不能因为里面有 `code=` 就当成信息门户的 code。
 */
fun classifyOauthRedirect(location: String): OauthRedirect? {
    val value = location.trim()
    if (value.isEmpty()) return null
    if (locationMatchesService(value, Constant.ONE_URL) && queryParameter(value, "code") != null) {
        return OauthRedirect.OneCode(value)
    }
    if (locationMatchesService(value, Constant.HUI_XIN_URL)) {
        val token = queryParameter(value, "synjones-auth") ?: return null
        return OauthRedirect.HuiXinAuth(token)
    }
    return null
}

/** 取查询串里名字整段相等的参数。不在别的参数值里面搜 `code=`。 */
fun queryParameter(url: String, name: String): String? {
    val query = runCatching { URI(url).rawQuery }.getOrNull() ?: return null
    for (pair in query.split('&')) {
        val eq = pair.indexOf('=')
        if (eq <= 0) continue
        if (pair.substring(0, eq) != name) continue
        return pair.substring(eq + 1).substringBefore('#').takeIf { it.isNotEmpty() }
    }
    return null
}

private fun absoluteLocation(request: Request, raw: String): String {
    val header = raw.trim()
    if (header.startsWith("http://", ignoreCase = true) || header.startsWith("https://", ignoreCase = true)) {
        return header
    }
    return request.url.resolve(header)?.toString() ?: header
}

private fun recordOauthRedirect(location: String) {
    when (val action = classifyOauthRedirect(location)) {
        is OauthRedirect.OneCode -> {
            LogUtil.debug("登录信息门户")
            GoToInterceptorState.toOneCode.value = action.location
        }
        is OauthRedirect.HuiXinAuth -> saveHuiXinAuth(action.token)
        null -> Unit
    }
}

private fun saveHuiXinAuth(token: String) {
    LogUtil.debug("登录慧新易校")
    SharedPrefs.saveString("auth", token)
    showToast("一卡通登录成功")
}
