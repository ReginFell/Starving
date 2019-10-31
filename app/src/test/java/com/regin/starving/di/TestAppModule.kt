package com.regin.starving.di

import com.regin.starving.core.async.DispatcherHolder
import com.regin.starving.core.location.LocationProvider
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.mockito.Mockito.mock

val testAppModule = module {
    single<LocationProvider> { mock(LocationProvider::class.java) }
    single { DispatcherHolder(Dispatchers.Unconfined, Dispatchers.Unconfined) }
}