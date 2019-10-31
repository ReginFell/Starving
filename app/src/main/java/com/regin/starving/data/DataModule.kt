package com.regin.starving.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.regin.starving.data.api.Api
import com.regin.starving.data.api.ApiVersion
import com.regin.starving.data.api.BaseUrl
import com.regin.starving.data.api.ClientId
import com.regin.starving.data.api.ClientSecret
import com.regin.starving.data.api.interceptor.AuthorizationInterceptor
import com.regin.starving.data.api.interceptor.CacheInterceptor
import com.regin.starving.data.repository.PoiRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

fun dataModule(
    baseUrl: BaseUrl,
    clientId: ClientId,
    clientSecret: ClientSecret,
    apiVersion: ApiVersion
) = module {

    single { AuthorizationInterceptor(clientId, clientSecret, apiVersion) }
    single { CacheInterceptor(get()) }
    single {
        HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthorizationInterceptor>())
            .addInterceptor(get<CacheInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        val contentType = CONTENT_TYPE_JSON.toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl.value)
            .addConverterFactory(Json.nonstrict.asConverterFactory(contentType))
            .client(get<OkHttpClient>())
            .build()

        retrofit.create(Api::class.java)
    }
    single { PoiRepository(get()) }
}

const val CONTENT_TYPE_JSON = "application/json"