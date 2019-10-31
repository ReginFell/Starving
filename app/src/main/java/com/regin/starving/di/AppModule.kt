package com.regin.starving.di

import com.regin.starving.core.async.DispatcherHolder
import com.regin.starving.core.location.LocationProvider
import com.regin.starving.location.GoogleLocationProvider
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

fun appModule() = module {
    single<LocationProvider> { GoogleLocationProvider(get()) }
    single { DispatcherHolder(Dispatchers.IO, Dispatchers.Main) }
}