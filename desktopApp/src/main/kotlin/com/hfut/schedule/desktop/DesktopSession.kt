package com.hfut.schedule.desktop

import com.hfut.schedule.logic.model.enumeration.CasLoginType
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * 登录状态持久化。
 *
 * CAS 登录成功后会把 TGC 写进偏好；下次启动时用 TGC 换一张新的教务 service ticket，
 * 再取回教务的 SESSION 并写回 `redirect`，从而免去重新输入账号密码与验证码。
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

    /** 清除已保存的登录状态 */
    fun clear() {
        runCatching {
            SharedPrefs.saveString(KEY_TGC, "")
            SharedPrefs.saveString(KEY_CAS_COOKIES, "")
            SharedPrefs.saveString(KEY_REDIRECT, "")
            GlobalUiStateHolder.casCookies = null
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
            val ticketUrl = client.newCall(
                Request.Builder()
                    .url(url)
                    .header("Cookie", tgcCookie)
                    .header("User-Agent", UA)
                    .build()
            ).execute().use { resp ->
                // 会话过期时 CAS 直接返回登录页（200），此时无法恢复
                if (resp.code != 302 && resp.code != 303) return@runCatching null
                val location = resp.header("Location") ?: return@runCatching null
                if (!location.contains("ticket=")) return@runCatching null
                location
            }

            // 跟随一次到两次跳转，从 Set-Cookie 里取教务 SESSION
            var target = ticketUrl
            var session: String? = null
            var hops = 0
            while (hops < 4 && session == null) {
                val current = target
                var next: String? = null
                client.newCall(
                    Request.Builder()
                        .url(current)
                        .header("Cookie", tgcCookie)
                        .header("User-Agent", UA)
                        .build()
                ).execute().use { resp ->
                    resp.headers("Set-Cookie").forEach { cookie ->
                        val value = cookie.substringAfter("SESSION=", "")
                        if (value.isNotEmpty() && session == null) {
                            session = value.substringBefore(";")
                        }
                    }
                    val location = resp.header("Location")
                    if (resp.code == 302 || resp.code == 303) next = location
                }
                if (session == null && next != null) {
                    target = next!!
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
}
