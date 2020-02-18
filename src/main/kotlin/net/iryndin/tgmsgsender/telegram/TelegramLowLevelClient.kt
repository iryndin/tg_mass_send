package net.iryndin.tgmsgsender.telegram

import mu.KLogging
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class TelegramLowLevelClientException(cause: Exception): Exception(cause)

data class TelegramResponse(
    val statusCode: Int,
    val body: String
) {
    fun isOk(): Boolean = (statusCode / 100) == 2
}

class TelegramLowLevelClient(
    private val token: String
) {

    private val client = OkHttpClient()

    fun get(methodName: String, params: Map<String, String>): TelegramResponse {
        try {
            val httpUrl = buildUrlWithParams(buildEndpointUrl(methodName), params)
            val request = Request.Builder().url(httpUrl).get().build()
            client.newCall(request).execute().use {
                return TelegramResponse(it.code, it.body!!.string())
            }
        } catch (ex: Exception) {
            throw TelegramLowLevelClientException(ex)
        }
    }

    private fun buildUrlWithParams(baseUrl: String, params: Map<String, String>): HttpUrl {
        val httpUrlBuilder = baseUrl.toHttpUrl().newBuilder()
        params.forEach { httpUrlBuilder.addQueryParameter(it.key, it.value) }
        return httpUrlBuilder.build()
    }

    private fun buildEndpointUrl(methodName: String): String = "$BOT_BASE_URL$token/$methodName"

    companion object: KLogging() {
        const val BOT_BASE_URL = "https://api.telegram.org/bot"
    }
}