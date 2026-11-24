package com.hfut.schedule.desktop

import com.hfut.schedule.logic.network.impl.casOauth
import com.hfut.schedule.logic.network.interceptor.GoToInterceptorState
import com.hfut.schedule.logic.network.interceptor.GotoInterceptor
import com.hfut.schedule.logic.network.interceptor.OauthRedirect
import com.hfut.schedule.logic.network.interceptor.classifyOauthRedirect
import com.hfut.schedule.logic.network.interceptor.follow
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.util.QWeatherCredentials
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.UniAppSessionAction
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.uniAppSessionAction
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * OAuth Cookie、跳转解析、和风天气私钥、合工大教务空 token。不联网。
 */
class DesktopConfirmedFixesTest {

    @Test
    fun oauthClientDoesNotAutoFollowRedirects() {
        val client = OkHttpClient.Builder().casOauth().build()
        assertFalse(client.followRedirects)
        assertFalse(client.followSslRedirects)
        assertTrue(client.interceptors.any { it is GotoInterceptor })
        assertTrue(client.networkInterceptors.none { it is GotoInterceptor })
    }

    @Test
    fun followDropsCasCookieWhenHostChanges() {
        val request = Request.Builder()
            .url(Constant.CAS_LOGIN_URL + "cas/oauth2.0/authorize")
            .header("Cookie", "TGC=secret-ticket")
            .build()
        val huiXin = Constant.HUI_XIN_URL +
            "berserker-auth/cas/oauth2url?oauth2url=${Constant.HUI_XIN_URL}berserker-base/redirect&code=hui-xin-code"
        val next = request.follow(huiXin)
        assertEquals("121.251.19.62", next.url.host)
        assertEquals("http", next.url.scheme)
        assertNull(next.header("Cookie"))

        val sameHost = request.follow(Constant.CAS_LOGIN_URL + "cas/login?ticket=ST-1")
        assertEquals("TGC=secret-ticket", sameHost.header("Cookie"))
    }

    @Test
    fun interceptorDoesNotSendCasCookieToHuiXinOrStoreItsCodeAsOne() {
        val huiXin = Constant.HUI_XIN_URL +
            "berserker-auth/cas/oauth2url?oauth2url=${Constant.HUI_XIN_URL}berserker-base/redirect&code=hui-xin-code"
        val start = Request.Builder()
            .url(Constant.CAS_LOGIN_URL + "cas/oauth2.0/authorize")
            .header("Cookie", "TGC=secret-ticket")
            .build()
        val previous = GoToInterceptorState.toOneCode.value
        try {
            GoToInterceptorState.toOneCode.value = null
            val chain = ScriptedChain(start, listOf(
                { request -> redirect(request, huiXin) },
                { request -> ok(request) },
            ))
            GotoInterceptor().intercept(chain).close()
            assertEquals(2, chain.seen.size)
            assertEquals("TGC=secret-ticket", chain.seen[0].header("Cookie"))
            assertNull(chain.seen[1].header("Cookie"))
            assertEquals("121.251.19.62", chain.seen[1].url.host)
            assertNull(GoToInterceptorState.toOneCode.value)
            assertNull(classifyOauthRedirect(huiXin))
        } finally {
            GoToInterceptorState.toOneCode.value = previous
        }
    }

    @Test
    fun oneCodeIsTheLocationHeaderAndHuiXinAuthIsTheQueryValue() {
        val one = Constant.ONE_URL + "home/index?code=OC-example"
        val huiXinAuth = Constant.HUI_XIN_URL + "berserker-base/redirect?synjones-auth=token-value"
        val huiXinThenMore = Constant.HUI_XIN_URL + "plat?synjones-auth=token-value&next=1"
        val listForm = listOf(huiXinAuth).toString()

        assertEquals(one, (classifyOauthRedirect(one) as OauthRedirect.OneCode).location)
        assertEquals("token-value", (classifyOauthRedirect(huiXinAuth) as OauthRedirect.HuiXinAuth).token)
        assertEquals("token-value", (classifyOauthRedirect(huiXinThenMore) as OauthRedirect.HuiXinAuth).token)
        assertEquals("token-value]", listForm.substringAfter("synjones-auth=").substringBefore("&"))
        assertNull(classifyOauthRedirect(listForm))
        assertNull(classifyOauthRedirect("https://evil.example/steal?code=OC-example"))

        val start = Request.Builder()
            .url(Constant.CAS_LOGIN_URL + "cas/oauth2.0/authorize")
            .header("Cookie", "TGC=secret-ticket")
            .build()
        val previous = GoToInterceptorState.toOneCode.value
        try {
            GoToInterceptorState.toOneCode.value = null
            val chain = ScriptedChain(start, listOf(
                { request -> redirect(request, one) },
                { request -> ok(request) },
            ))
            GotoInterceptor().intercept(chain).close()
            assertEquals(one, GoToInterceptorState.toOneCode.value)
            assertNull(chain.seen[1].header("Cookie"))
        } finally {
            GoToInterceptorState.toOneCode.value = previous
        }
    }

    @Test
    fun emptyUniAppTokenUsesPasswordLogin() {
        assertEquals(UniAppSessionAction.Login, uniAppSessionAction("", null))
        assertEquals(UniAppSessionAction.Login, uniAppSessionAction("", StatusCode.OK.code))
        assertEquals(UniAppSessionAction.Login, uniAppSessionAction("jwt", StatusCode.UNAUTHORIZED.code))
        assertEquals(UniAppSessionAction.Keep, uniAppSessionAction("jwt", StatusCode.OK.code))
        assertEquals(UniAppSessionAction.Keep, uniAppSessionAction("jwt", StatusCode.INTERNAL_SERVER_ERROR.code))
    }

    @Test
    fun qweatherKeyIsLoadedFromOutsideTheApp() {
        assertNull(QWeatherCredentials.load(sys = { null }, env = { null }, file = null))
        assertNull(
            QWeatherCredentials.load(
                sys = { key -> if (key == QWeatherCredentials.PROP_PRIVATE_KEY) "only-one" else null },
                env = { null },
                file = null,
            )
        )

        val file = File(System.getProperty("java.io.tmpdir"), "hfut-qweather-" + System.nanoTime() + ".properties")
        try {
            file.writeText(
                "privateKey=local-test-material\nprojectId=project\nkeyId=kid\n"
            )
            val fromFile = QWeatherCredentials.load(sys = { null }, env = { null }, file = file)
            assertEquals("local-test-material", fromFile!!.privateKeyPkcs8)
            assertEquals("project", fromFile.projectId)
            assertEquals("kid", fromFile.keyId)

            val fromProp = QWeatherCredentials.load(
                sys = { key ->
                    when (key) {
                        QWeatherCredentials.PROP_PRIVATE_KEY -> "from-prop"
                        QWeatherCredentials.PROP_PROJECT_ID -> "p2"
                        QWeatherCredentials.PROP_KEY_ID -> "k2"
                        else -> null
                    }
                },
                env = { null },
                file = file,
            )
            assertEquals("from-prop", fromProp!!.privateKeyPkcs8)
            assertEquals("p2", fromProp.projectId)
            assertEquals("k2", fromProp.keyId)
        } finally {
            file.delete()
        }
    }

    @Test
    fun qweatherSignerSourceHasNoEmbeddedKey() {
        val crypto = findRepoFile(
            "network-api/src/jvmShared/kotlin/com/hfut/schedule/network/api/util/CryptoUtil.kt"
        )
        val generator = findRepoFile(
            "network-api/src/jvmShared/kotlin/com/hfut/schedule/network/api/util/QWeatherKeyGenerator.java"
        )
        val credentials = findRepoFile(
            "network-api/src/jvmShared/kotlin/com/hfut/schedule/network/api/util/QWeatherCredentials.kt"
        )
        val base64Run = Regex("[A-Za-z0-9+/]{50,}={0,2}")
        assertFalse(base64Run.containsMatchIn(generator.readText()))
        assertFalse(base64Run.containsMatchIn(credentials.readText()))
        val body = functionBody(crypto.readText(), "getQWeatherAuth")
        assertFalse(body.contains("generate(\""))
        assertFalse(base64Run.containsMatchIn(body))
        assertTrue(body.contains("QWeatherCredentials.load()"))
    }
}

private fun emptyBody() = ByteArray(0).toResponseBody(null)

private fun redirect(request: Request, location: String): Response =
    Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(302)
        .message("Found")
        .header("Location", location)
        .body(emptyBody())
        .build()

private fun ok(request: Request): Response =
    Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(emptyBody())
        .build()

private class ScriptedChain(
    initial: Request,
    steps: List<(Request) -> Response>,
) : Interceptor.Chain {
    private val steps = steps.toMutableList()
    val seen = mutableListOf<Request>()
    private var current = initial

    override fun request(): Request = current

    override fun proceed(request: Request): Response {
        seen += request
        current = request
        return steps.removeAt(0)(request)
    }

    override fun connection(): Connection? = null

    override fun call(): Call = error("unused")

    override fun connectTimeoutMillis(): Int = 0

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun readTimeoutMillis(): Int = 0

    override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun writeTimeoutMillis(): Int = 0

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
}

private fun findRepoFile(relative: String): File {
    var dir: File? = File("").absoluteFile
    while (dir != null) {
        val candidate = File(dir, relative)
        if (candidate.isFile) return candidate
        dir = dir.parentFile
    }
    error("找不到 $relative")
}

private fun functionBody(source: String, name: String): String {
    val start = source.indexOf("fun $name")
    check(start >= 0) { name }
    val next = source.indexOf("\n    fun ", start + 1).let { if (it < 0) source.length else it }
    return source.substring(start, next)
}
