package com.regin.starving.feature.explore

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val exploreModule = module {
    viewModel { ExploreViewModel(get(), get(), get()) }
}