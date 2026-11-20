package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.util.network.encodeUrl
import com.hfut.schedule.logic.util.network.isNotBadRequest
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.util.WebVpnConvertor
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.util.LogUtil
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URI

class RedirectTicketInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.isRedirect) {
            val rawLocation = response.header("Location") ?: return response
            val location = request.url.resolve(rawLocation)?.toString() ?: return response
            val ticket = extractCasTicket(location).orEmpty()
            LogUtil.debug("ticket=$ticket")
            when {
                hit(location,Constant.STU_URL) -> {
                    // 学工系统的登录
                    LogUtil.debug("登录学工系统")
                    // 向前重定向一次
                    val nextResponse = chain.proceed(request.follow(location))
                    parseLoginStu(nextResponse.headers,nextResponse.body?.string())
                    nextResponse.close()
                }
                hit(location,Constant.COMMUNITY_URL) -> {
                    // 智慧社区的登录
                    LogUtil.debug("登录智慧社区")
                    if (ticket.isNotEmpty()) GoToInterceptorState.toCommunityTicket.value = ticket
                }
                hit(location,Constant.ZHI_JIAN_URL) -> {
                    // 指间工大登录
                    LogUtil.debug("登录指间工大")
                    // 向前重定向一次
                    val nextResponse = chain.proceed(request.follow(location))
                    val cookie = parseLoginZhiJian(nextResponse.headers)
                    val homeLocation = nextResponse.header("Location")
                    nextResponse.close()
                    cookie?.let {
                        // 向主页发送一个请求 使cookie生效
                        val checkRequest = request
                            .newBuilder()
                            .header("Cookie", cookie)
                            .url(
                                trustedHome(location, homeLocation) ?: (
                                        Constant.ZHI_JIAN_URL +
                                                "wui/cas-entrance.jsp;jsessionid=${
                                                    it.substringAfter("=")
                                                }?path=${
                                                    encodeUrl(encodeUrl(Constant.ZHI_JIAN_URL + "wui/index.html#/main"))
                                                }&ssoType=CAS"
                                        )
                            )
                            .build()
                        val checkResponse = chain.proceed(checkRequest)
                        LogUtil.debug("CAS 指尖工大登录 ${checkResponse.code}")
                        if(isNotBadRequest(checkResponse.code)) {
                            showToast("指间工大登陆成功")
                        } else {
                            showToast("指间工大登陆失败 ${checkResponse.code}")
                        }
                        checkResponse.close()
                    }
                }
                hit(location,Constant.JXGLSTU_URL) -> {
                    // 教务系统的登录
                    LogUtil.debug("登录教务系统")
                    // 向前重定向一次
                    val nextResponse = chain.proceed(request.follow(location))
                    parseLoginJxglstu(nextResponse.headers)
                    nextResponse.close()
                }
                hit(location,Constant.NEW_LIBRARY_URL) -> {
                    // 图书馆登录
                    LogUtil.debug("登录图书馆")
                    // 向前重定向一次
                    val nextResponse = chain.proceed(request.follow(location))
                    parseLoginLibrary(nextResponse.headers)
                    nextResponse.close()
                }
                hit(location,Constant.PE_URL) -> {
                    // 体测平台
                    LogUtil.debug("登录体测平台")
                    if (ticket.isNotEmpty()) {
                        val token = "PHPSESSID=$ticket"
                        saveString("PE", token)
                        // 向前重定向一次
                        // 直到响应不是302
                        val landed = request.follow(location)
                        val nextResponse = chain.proceed(landed)
                        val location2 = nextResponse.headers["Location"]
                        nextResponse.close()
                        val nextHop = location2?.let { landed.follow(it) }
                        if (nextHop != null && nextHop.url.host.equals(landed.url.host, ignoreCase = true)) {
                            val nextResponse2 = chain.proceed(nextHop.newBuilder().header("Cookie", token).build())
                            nextResponse2.close()
                            showToast("体测平台登陆成功")
                        } else {
                            showToast("体测平台登录失败")
                        }
                    }
                }
                hit(location,Constant.SECOND_CLASS_URL) -> {
                    // 二课登录
                    LogUtil.debug("登录第二课堂")
                    // 向前重定向一次
                    val nextResponse = chain.proceed(request.follow(location))
                    val cookie = parseLoginSecondClass(nextResponse.headers)
                    val homeLocation = nextResponse.header("Location")
                    nextResponse.close()
                    cookie?.let {
                        // 向主页发送一个请求 使cookie生效
                        val checkRequest = request
                            .newBuilder()
                            .header("Cookie", cookie)
                            .url(
                                trustedHome(location, homeLocation)
                                    ?: (Constant.SECOND_CLASS_URL + "scReports/uccp_index")
                            )
                            .build()
                        val checkResponse = chain.proceed(checkRequest)
                        LogUtil.debug("CAS 第二课堂登录 ${checkResponse.code}")
                        if(isNotBadRequest(checkResponse.code)) {
                            showToast("第二课堂登陆成功")
                        } else {
                            showToast("第二课堂登陆失败 ${checkResponse.code}")
                        }
                        checkResponse.close()
                    }
                }
            }
        }
        return response
    }
}

/**
 * 只认「主机 + 端口 + 路径前缀」都落在目标服务上的跳转。
 * 以前用 `location.contains(目标 URL)`，查询串里拼一段学校地址就能命中，
 * 随后原请求的 Cookie（含 CAS 会话）会被原样带到攻击者的主机。
 */
fun locationMatchesService(location: String, serviceBase: String): Boolean {
    val current = runCatching { URI(location.trim()) }.getOrNull() ?: return false
    val target = runCatching { URI(serviceBase.trim()) }.getOrNull() ?: return false
    val currentScheme = current.scheme?.lowercase() ?: return false
    val targetScheme = target.scheme?.lowercase() ?: return false
    if (currentScheme != "http" && currentScheme != "https") return false
    if (targetScheme != "http" && targetScheme != "https") return false
    if (current.userInfo != null) return false
    val currentHost = current.host?.lowercase() ?: return false
    val targetHost = target.host?.lowercase() ?: return false
    if (currentHost != targetHost) return false
    if (target.port != -1 && effectivePort(current) != target.port) return false
    val targetPath = directoryPrefix(target.path)
    val currentPath = directoryPrefix(current.path)
    return currentPath.startsWith(targetPath)
}

/** 从一条 Set-Cookie 里取出指定名字的值；名字必须整段相等，避免把 JSESSIONID 当成 SESSION。 */
fun setCookieValue(setCookie: String, name: String): String? {
    val pair = setCookie.substringBefore(';').trim()
    val eq = pair.indexOf('=')
    if (eq <= 0) return null
    if (!pair.substring(0, eq).trim().equals(name, ignoreCase = true)) return null
    return pair.substring(eq + 1).trim().takeIf { it.isNotEmpty() }
}

/** 从 CAS 的 Location 里取 ticket 参数。服务地址本身带 `=` 时不能用「第一个等号后面」。 */
fun extractCasTicket(location: String?): String? {
    if (location.isNullOrBlank()) return null
    val marker = "ticket="
    val index = location.indexOf(marker)
    if (index < 0) return null
    return location.substring(index + marker.length)
        .substringBefore('&')
        .substringBefore('#')
        .takeIf { it.isNotEmpty() }
}

/**
 * 从全部 Set-Cookie 里找 TGC，返回 `TGC=值`。
 * `Headers.get` 只返回第一条，第一条经常是 JSESSIONID；旧逻辑会把那一条整段当成 TGC 存下来。
 * 带上名字是因为课表刷新把这个字符串直接拼进 Cookie 头。
 */
fun extractCasTgc(setCookies: List<String>): String? =
    setCookies.firstNotNullOfOrNull { setCookieValue(it, "TGC") }?.let { "TGC=$it" }

private fun hit(current : String,target : String) : Boolean =
    locationMatchesService(current, target) ||
        locationMatchesService(current, WebVpnConvertor.getWebVpnUrl(target))

/** 换主机时丢掉原请求的 Cookie，避免把 CAS 会话送到教务 / 图书馆等其它站点。 */
private fun Request.follow(location: String): Request {
    val resolved = url.resolve(location) ?: return newBuilder().url(location).removeHeader("Cookie").build()
    val builder = newBuilder().url(resolved)
    if (!resolved.host.equals(url.host, ignoreCase = true)) builder.removeHeader("Cookie")
    return builder.build()
}

/** 只跟随仍落在刚才那个服务上的 Location；否则用调用方准备的固定地址。 */
private fun trustedHome(current: String, location: String?): String? {
    if (location.isNullOrBlank()) return null
    val resolved = runCatching { URI(current).resolve(location).toString() }.getOrNull() ?: return null
    val currentHost = runCatching { URI(current).host }.getOrNull() ?: return null
    val nextHost = runCatching { URI(resolved).host }.getOrNull() ?: return null
    val scheme = runCatching { URI(resolved).scheme?.lowercase() }.getOrNull()
    if (scheme != "http" && scheme != "https") return null
    return resolved.takeIf { nextHost.equals(currentHost, ignoreCase = true) }
}

private fun effectivePort(uri: URI): Int {
    if (uri.port != -1) return uri.port
    return if (uri.scheme.equals("https", ignoreCase = true)) 443 else 80
}

private fun directoryPrefix(path: String?): String {
    val raw = path?.takeIf { it.isNotEmpty() } ?: "/"
    return if (raw.endsWith("/")) raw else "$raw/"
}

private fun parseLoginStu(headers: Headers,json: String?) =  try {
    if(json == null) throw Exception("无内容")

    val sId = getPersonInfo().getStudentIdFinally() ?: throw Exception("无学号")
    if(!json.contains(sId)) throw Exception("登陆失败")
    val weu = headers.values("Set-Cookie").firstNotNullOfOrNull { setCookieValue(it, "_WEU") }
        ?: throw Exception("解析失败")

    saveString("stu", "_WEU=$weu")
    LogUtil.debug("学工系统登录 _WEU")
    showToast("学工系统登陆成功")
} catch (e : Exception) {
    LogUtil.error(e)
}


private fun parseLoginJxglstu(headers: Headers) = try {
    val cookie = headers.values("Set-Cookie").firstNotNullOfOrNull { setCookieValue(it, "SESSION") }
    if (cookie == null) {
        showToast("教务系统登陆失败")
    } else {
        saveString("redirect", "SESSION=$cookie")
        showToast("教务系统登陆成功")
    }
} catch (e : Exception) {
    LogUtil.error(e)
}

private fun parseLoginZhiJian(headers: Headers) : String? = try {
    val token = headers["Location"]?.substringAfter("jsessionid=")?.substringBefore("?")
    if (token != null && !token.contains("://") && !token.contains("/")) {
        val r = "ecology_JSessionid=$token"
        saveString("ZhiJian", r)
        r
    } else {
        showToast("指间工大登陆失败")
        null
    }
} catch (e : Exception) {
    LogUtil.error(e)
    null
}

private fun parseLoginLibrary(headers: Headers)  = try {
    val token = headers.values("Set-Cookie").firstNotNullOfOrNull { setCookieValue(it, "Authorization") }
    if (token != null && token.contains("ey")) {
        saveString(LIBRARY_TOKEN, "Bearer $token")
        showToast("图书馆登陆成功")
    } else {
        showToast("图书馆登陆失败")
    }
} catch (e : Exception) {
    LogUtil.error(e)
}


private fun parseLoginSecondClass(headers: Headers) : String? = try {
    val cookie = headers.values("Set-Cookie").firstNotNullOfOrNull { line ->
        line.takeIf { setCookieValue(it, "SESSION") != null }?.substringBefore(";")
    }
    LogUtil.debug("登录第二课堂")

    if (cookie != null) {
        saveString(SharedPrefs.SECOND_CLASS_TOKEN, cookie)
        cookie
    } else {
        showToast("第二课堂登陆失败")
        null
    }
} catch (e : Exception) {
    LogUtil.error(e)
    null
}
