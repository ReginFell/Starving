package com.regin.starving.data.api.interceptor

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CacheInterceptor(private val context: Context) : Interceptor {

    companion object {
        private val CACHE_LIFETIME_SECONDS = TimeUnit.SECONDS.toSeconds(30)
        private val CACHE_LIFETIME_OFFLINE_SECONDS = TimeUnit.DAYS.toSeconds(5)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().apply {
            if (isNetworkAvailable())
                newBuilder().header(
                    "Cache-Control",
                    "public, max-age=$CACHE_LIFETIME_SECONDS"
                ).build()
            else
                newBuilder().header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=$CACHE_LIFETIME_OFFLINE_SECONDS"
                ).build()
        }
        return chain.proceed(request)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}

