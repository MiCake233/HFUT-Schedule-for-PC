package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.GotoInterceptor
import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.allowAutoRedirect
import okhttp3.OkHttpClient

/**
 * 信息门户 / 慧新易校的 CAS OAuth 客户端。
 *
 * 不跟随自动重定向。OkHttp 4.9 在换主机时不会去掉 Cookie，慧新易校的跳转目标是另一台明文主机。
 * [GotoInterceptor] 自己跟随，并在换主机时丢掉 CAS Cookie。
 */
fun OkHttpClient.Builder.casOauth(): OkHttpClient.Builder = this
    .allowAutoRedirect(false)
    .addInterceptor(GotoInterceptor())

object OneGotoServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder().casOauth().build()
)
