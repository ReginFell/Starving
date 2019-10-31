package com.regin.starving.core.async

import kotlinx.coroutines.CoroutineDispatcher

data class DispatcherHolder(
    val IO: CoroutineDispatcher,
    val main: CoroutineDispatcher
)