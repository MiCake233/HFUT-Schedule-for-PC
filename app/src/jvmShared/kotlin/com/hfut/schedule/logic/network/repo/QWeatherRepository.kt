package com.hfut.schedule.logic.network.repo


import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNow
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherNowResponse
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherWarning
import com.hfut.schedule.network.api.model.response.json.qweather.QWeatherWarnResponse
import com.hfut.schedule.network.api.inf.QWeatherService
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.network.api.impl.QWeatherServiceCreator
import com.hfut.schedule.network.api.repo.QWeatherRepositoryInf
import com.hfut.schedule.network.api.util.CryptoUtil
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.ui.screen.home.search.function.other.life.getLocation
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call

object QWeatherRepository : QWeatherRepositoryInf {
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)

    override suspend fun getWeatherWarn(campus: CampusRegion, weatherWarningData : UiStateHolder<QWeatherWarnResponse>) =
        withWeatherAuth(
            holder = weatherWarningData,
            request = { auth -> qWeather.getWeatherWarn(locationID = getLocation(campus), authorization = auth) },
            transformSuccess = { _, json -> parseWeatherWarn(json) }
        )

    @JvmStatic
    private fun parseWeatherWarn(json : String) : QWeatherWarnResponse = try {
        GsonInstance.fromJson(json, QWeatherWarnResponse::class.java)
    } catch (e : Exception) { throw e }

    override suspend fun getWeather(campus: CampusRegion, qWeatherResult : UiStateHolder<QWeatherNowResponse>) =
        withWeatherAuth(
            holder = qWeatherResult,
            request = { auth -> qWeather.getWeather(locationID = getLocation(campus), authorization = auth) },
            transformSuccess = { _, json -> parseWeatherNow(json) }
        )

    private suspend fun <T> withWeatherAuth(
        holder: UiStateHolder<T>,
        request: suspend (auth: String) -> Call<ResponseBody>,
        transformSuccess: suspend (Headers, String) -> T,
    ) {
        val auth = CryptoUtil.getQWeatherAuth()
        if (auth.isBlank()) {
            holder.emitError(IllegalStateException("未配置和风天气签名"))
            return
        }
        launchRequestState(holder = holder, request = { request(auth) }, transformSuccess = transformSuccess)
    }

    @JvmStatic
    private fun parseWeatherNow(json : String) : QWeatherNowResponse = try {
        if(json.contains(StatusCode.OK.code.toString()))
            GsonInstance.fromJson(json, QWeatherNowResponse::class.java)
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

}