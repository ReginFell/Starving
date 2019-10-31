package com.regin.starving.data.api.interceptor

import com.regin.starving.data.api.ApiVersion
import com.regin.starving.data.api.ClientId
import com.regin.starving.data.api.ClientSecret
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    private val clientId: ClientId,
    private val clientSecret: ClientSecret,
    private val apiVersion: ApiVersion
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val original = chain.request()
        val originalHttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter(CLIENT_ID, clientId.value)
            .addQueryParameter(CLIENT_SECRET, clientSecret.value)
            .addQueryParameter(VERSION, apiVersion.value)
            .build()

        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val CLIENT_ID = "client_id"
        private const val CLIENT_SECRET = "client_secret"
        private const val VERSION = "v"
    }
}