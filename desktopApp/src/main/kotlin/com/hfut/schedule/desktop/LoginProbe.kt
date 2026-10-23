package com.hfut.schedule.desktop

import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.LogUtil
import com.xah.common.platform.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.io.File

/**
 * CAS 登录链路自检（`--login-probe`）。
 *
 * 只做诊断，不写业务逻辑：把登录前必须就绪的几个前置条件逐个打印出来，
 * 便于定位「点登录一直转圈」到底卡在哪一步。
 */
fun runLoginProbe() {
    println("=== CAS 登录链路自检 ===")
    // 先探 LogUtil：共享代码里到处在调，桌面端如果连它都初始化失败，
    // 任何 LogUtil.error(e) 都会变成 NoClassDefFoundError 把流程带崩。
    runCatching { com.xah.common.logic.util.LogUtil.debug("probe-start") }
        .onFailure { e ->
            println("LOGUTIL_INIT_FAIL: " + e)
            var c: Throwable? = e
            while (c != null) {
                println("  cause: " + c + "  @ " + c.stackTrace.take(4).joinToString(" <- "))
                c = c.cause
            }
        }
        .onSuccess { println("LOGUTIL_OK") }

    println("dataDir = " + Platform.dataDirPath())

    val prefsDir = File(Platform.dataDirPath(), "prefs")
    prefsDir.listFiles()?.forEach { f -> println("prefs file: ${f.name}  ${f.length()} bytes") }

    // 1) 主线程调度器是否可用（runOnMain 用的就是 Dispatchers.Main）
    val mainOk = runBlocking {
        withTimeoutOrNull(3_000) {
            runCatching { withContext(Dispatchers.Main) { } }.isSuccess
        } ?: false
    }
    println("MAIN_DISPATCHER_OK=" + mainOk)

    val loginVm = LoginViewModel()
    val networkVm = NetWorkViewModel()

    // 2) 前置请求：AES key（顺带由拦截器写入 LOGIN_FLAVORING）+ CAS cookie/execution
    runBlocking {
        val keyResult = runCatching { loginVm.getKey() }
        println("getKey() -> " + keyResult.exceptionOrNull()?.let { it::class.simpleName + ": " + it.message })
        val jState = loginVm.jSessionId.state.value
        println("jSessionId = " + describe(jState))

        val cookieResult = runCatching { loginVm.getCookie() }
        println("getCookie() -> " + cookieResult.exceptionOrNull()?.let { it::class.simpleName + ": " + it.message })
        println("executionAndSession = " + describe(loginVm.executionAndSession.state.value))
        println("networkVm.checkJxglstuCanUse = " + runCatching { networkVm.checkJxglstuCanUse() })
    }

    // 3) LOGIN_FLAVORING 是否可被登录页读到
    val fromPrefs = prefs.getString("LOGIN_FLAVORING", "")
    println("prefs(LOGIN_FLAVORING) = [" + fromPrefs + "]  len=" + (fromPrefs?.length ?: -1))

    // 4) 验证码图片：桌面端不走 OCR，但图必须真的能解码显示（否则用户看不到验证码）
    val jSessionCookie = (loginVm.jSessionId.state.value as? NetworkUiState.Success)?.data?.jSession
    runBlocking {
        val info = withContext(Dispatchers.IO) {
            runCatching {
                val url = com.hfut.schedule.network.api.model.Constant.CAS_LOGIN_URL +
                    "cas/vercode?timestamp=" + System.currentTimeMillis()
                val req = Request.Builder().url(url).apply {
                    jSessionCookie?.let { header("Cookie", it) }
                }.build()
                OkHttpClient().newCall(req).execute().use { resp ->
                    val bytes = resp.body?.bytes() ?: ByteArray(0)
                    // 和 rememberImageState 桌面实现完全一致的解码路径
                    val decoded = runCatching { bytes.decodeToImageBitmap() }
                    val decodeInfo = decoded.fold(
                        onSuccess = { "${it.width}x${it.height}" },
                        onFailure = { "decode失败: " + it::class.simpleName + ": " + it.message }
                    )
                    "status=${resp.code} type=${resp.header("Content-Type")} bytes=${bytes.size} decode=$decodeInfo"
                }
            }.getOrElse { it::class.simpleName + ": " + it.message }
        }
        println("captcha fetch -> " + info)
    }

    // 5) 用假账号走一遍 login，看 code 是否会（无论成功失败）被回调置位
    val cookie = prefs.getString("LOGIN_FLAVORING", "") ?: ""
    val fakeUsername = "2023000000"
    val outputAes = runCatching {
        com.hfut.schedule.network.api.util.CryptoUtil.encryptAES("test-password", cookie)
    }.getOrElse { null }
    println("encryptAES(with cookie) = " + if (outputAes == null) "null" else "ok(len=" + outputAes.length + ")")

    if (outputAes != null) {
        runBlocking {
            val before = loginVm.code.value
            runCatching {
                withTimeoutOrNull(20_000) {
                    loginVm.login(fakeUsername, outputAes, "LOGIN_FLAVORING=$cookie", "")
                }
            }
            // 等异步回调
            val got = withTimeoutOrNull(20_000) {
                while (loginVm.code.value == before) delay(200)
                loginVm.code.value
            }
            println("login callback code = " + loginVm.code.value)
            println("login location = " + loginVm.location.value)
            println("LOGIN_CALLBACK_RECEIVED=" + (got != null))
        }
    } else {
        println("LOGIN_SKIPPED: outputAES 为 null（说明 LOGIN_FLAVORING 没拿到），这正是「一直转圈」的原因")
    }

}

private fun describe(state: Any?): String = when (state) {
    null -> "null"
    is NetworkUiState.Success<*> -> "Success(" + state.data.toString().take(120) + ")"
    is NetworkUiState.Error<*> -> "Error(" + state.code + "," + state.exception?.let { it::class.simpleName + ":" + it.message } + ")"
    is NetworkUiState.Loading -> "Loading"
    else -> state.toString()
}
