package com.regin.starving.domain

import com.regin.starving.domain.usecase.LoadRestaurantsUseCase
import org.koin.dsl.module

fun domainModule() = module {
    single { LoadRestaurantsUseCase(get()) }
}
