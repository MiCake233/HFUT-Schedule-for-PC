package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * 取当前 WebVPN 的 Cookie 头。
 * 原先定义在 WebVpnScreen.kt（Android UI 文件）里，但网络层与跳转逻辑都要用，抽到共享源集。
 */
suspend fun getWebVpnCookie(): String? =
    if (GlobalUiStateHolder.webVpn) {
        val webVpnCookie = withContext(Dispatchers.IO) {
            DataStoreManager.webVpnCookies.first { it.isNotEmpty() }
        }
        Constant.WEBVPN_COOKIE_HEADER + webVpnCookie
    } else {
        null
    }
