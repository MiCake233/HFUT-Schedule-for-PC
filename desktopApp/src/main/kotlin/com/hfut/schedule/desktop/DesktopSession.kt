package com.hfut.schedule.desktop

import com.hfut.schedule.logic.model.enumeration.CasLoginType
import com.hfut.schedule.logic.network.interceptor.setCookieValue
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * 登录状态持久化。
 *
 * 登录成功后 CAS 写入 TGC，保存于本地偏好。下次启动时以 TGC 换取新的教务 service ticket，
 * 取回教务 SESSION 并写回 `redirect`，从而免去重复的账号密码与验证码鉴权。
 */
object DesktopSession {

    private const val KEY_TGC = "TGC"
    private const val KEY_CAS_COOKIES = "casCookies"
    private const val KEY_REDIRECT = "redirect"

    private const val UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/131.0.0.0 Safari/537.36"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private fun prefs() = SharedPrefs.prefs

    private fun hasTgc(): Boolean = (prefs().getString(KEY_TGC, "") ?: "").isNotBlank()

    /** 本地是否存在可用的登录凭据 */
    fun hasSavedSession(): Boolean = hasTgc()

    /** 登录成功后保存会话，供下次启动恢复 */
    fun save(casCookies: String?) {
        runCatching {
            if (!casCookies.isNullOrBlank()) {
                SharedPrefs.saveString(KEY_CAS_COOKIES, casCookies)
            }
        }
    }

    /** 本机保存过的全部登录凭据键（退出登录时一起清掉，避免残留 token 继续可用） */
    private val CREDENTIAL_KEYS = listOf(
        KEY_TGC,
        KEY_CAS_COOKIES,
        KEY_REDIRECT,
        "ticket",          // CAS 票据
        "auth",            // 一卡通 / 微信 auth
        "bearer",          // 一网通办 bearer
        "TOKEN",           // 社区登录 token
        "LibraryToken",    // 图书馆
        "second_class_token",
        "SupabasePsk",     // 社区（supabase）登录密码
        "loginCode",       // 呱呱洗浴
        "PE",              // 体育
        "ZhiJian",
        "stu",             // 学工 _WEU
        "LOGIN_FLAVORING",
        "webVpnKey",
    )

    /** 退出登录时会清掉的键，供测试核对「没有漏网的会话」 */
    internal val credentialKeysClearedOnLogout: List<String> get() = CREDENTIAL_KEYS

    /** 清除已保存的登录状态 */
    fun clear() {
        runCatching {
            CREDENTIAL_KEYS.forEach { SharedPrefs.saveString(it, "") }
            SharedPrefs.saveString("Password", "")
            GlobalUiStateHolder.casCookies = null
            runBlocking { DataStoreManager.clearSessionSecrets() }
        }
    }

    /**
     * 用 TGC 恢复登录态。
     * @return 恢复成功返回教务 SESSION（`SESSION=xxx`），失败返回 null
     */
    suspend fun restore(): String? = withContext(Dispatchers.IO) {
        val tgc = prefs().getString(KEY_TGC, "") ?: ""
        if (tgc.isBlank()) return@withContext null

        val casCookies = prefs().getString(KEY_CAS_COOKIES, "") ?: ""
        if (casCookies.isNotBlank()) GlobalUiStateHolder.casCookies = casCookies
        // 存下来的值本身可能已带 "TGC=" 前缀（取自 Set-Cookie 的整段）
        val tgcCookie = if (tgc.startsWith("TGC=", ignoreCase = true)) tgc else "TGC=$tgc"

        runCatching {
            val service = CasLoginType.JXGLSTU.service ?: return@runCatching null
            val url = Constant.CAS_LOGIN_URL + "cas/login?service=" + URLEncoder.encode(service, "UTF-8")
            val first = hop(url, tgcCookie)
            // 会话过期时 CAS 直接返回登录页（200），此时无法恢复
            val ticketUrl = first.next ?: return@runCatching null
            if (!ticketUrl.contains("ticket=")) return@runCatching null

            // 跟随一次到两次跳转，从 Set-Cookie 里取教务 SESSION。
            // TGC 只发给 CAS 主机；教务那边只需要 URL 里的 ticket。
            var target = ticketUrl
            var session: String? = null
            var hops = 0
            while (hops < 4 && session == null) {
                val current = target
                val result = hop(current, cookieFor(current, tgcCookie))
                session = result.session
                val next = result.next
                if (session == null && next != null) {
                    target = next
                } else {
                    break
                }
                hops++
            }

            val value = session ?: return@runCatching null
            val redirect = "SESSION=$value"
            SharedPrefs.saveString(KEY_REDIRECT, redirect)
            redirect
        }.getOrNull()
    }

    private data class Hop(val session: String?, val next: String?)

    /** 请求一跳。Location 必须落在 CAS 或教务主机上，否则丢掉，不把 TGC 送出去。 */
    private fun hop(url: String, cookie: String?): Hop {
        if (!isTrustedSessionUrl(url)) return Hop(null, null)
        val builder = Request.Builder().url(url).header("User-Agent", UA)
        if (cookie != null) builder.header("Cookie", cookie)
        client.newCall(builder.build()).execute().use { resp ->
            val session = resp.headers("Set-Cookie").firstNotNullOfOrNull { setCookieValue(it, "SESSION") }
            val location = if (resp.code == 302 || resp.code == 303) resp.header("Location") else null
            val next = location?.let { resolveTrusted(url, it) }
            return Hop(session, next)
        }
    }

    private fun cookieFor(url: String, tgcCookie: String): String? =
        if (hostOf(url) == hostOf(Constant.CAS_LOGIN_URL)) tgcCookie else null
}

/** 恢复登录时允许跟随的地址：仅 CAS 与教务系统，且不能带 userinfo。 */
internal fun isTrustedSessionUrl(url: String): Boolean {
    val uri = runCatching { URI(url) }.getOrNull() ?: return false
    val scheme = uri.scheme?.lowercase() ?: return false
    if (scheme != "http" && scheme != "https") return false
    if (uri.userInfo != null) return false
    val host = uri.host?.lowercase() ?: return false
    return host in sessionHosts()
}

internal fun resolveTrusted(currentUrl: String, location: String): String? {
    val resolved = runCatching { URI(currentUrl).resolve(location).toString() }.getOrNull() ?: return null
    return resolved.takeIf { isTrustedSessionUrl(it) }
}

private fun sessionHosts(): Set<String> = setOfNotNull(
    hostOf(Constant.CAS_LOGIN_URL),
    CasLoginType.JXGLSTU.service?.let { hostOf(it) },
)

private fun hostOf(url: String): String? =
    runCatching { URI(url).host?.lowercase() }.getOrNull()
