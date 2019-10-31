package com.regin.starving

import android.app.Application
import com.regin.starving.data.api.ApiVersion
import com.regin.starving.data.api.BaseUrl
import com.regin.starving.data.api.ClientId
import com.regin.starving.data.api.ClientSecret
import com.regin.starving.data.dataModule
import com.regin.starving.di.appModule
import com.regin.starving.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)

            modules(
                listOf(
                    appModule(),
                    dataModule(
                        BaseUrl(BuildConfig.BASE_URL),
                        ClientId(BuildConfig.CLIENT_ID),
                        ClientSecret(BuildConfig.CLIENT_SECRET),
                        ApiVersion(BuildConfig.API_VERSION)
                    ),
                    domainModule()
                )
            )
        }
    }
}