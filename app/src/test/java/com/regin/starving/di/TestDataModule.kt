package com.regin.starving.di

import com.regin.starving.data.api.Api
import com.regin.starving.data.repository.PoiRepository
import org.koin.dsl.module
import org.mockito.Mockito.mock

val testDataModule = module {
    single { mock(Api::class.java) }
    single { PoiRepository(get()) }
}