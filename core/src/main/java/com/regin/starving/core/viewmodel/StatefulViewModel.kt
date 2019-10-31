package com.regin.starving.core.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

@Suppress("EXPERIMENTAL_API_USAGE")
abstract class StatefulViewModel<VS, E, SE> : ViewModel() {

    abstract val defaultViewState: VS

    private val _viewState = ConflatedBroadcastChannel(defaultViewState)

    val viewState: Flow<VS> get() = _viewState.openSubscription().consumeAsFlow()
    val currentState: VS
        get() = _viewState.value

    private val _sideEffects = BroadcastChannel<SE>(Channel.BUFFERED)
    val sideEffects: Flow<SE> get() = _sideEffects.openSubscription().consumeAsFlow()

    private val _events = Channel<E>(Channel.BUFFERED)
    val events get() = _events

    fun dispatchEvent(event: E) {
        events.offer(event)
    }

    fun dispatchSideEffect(sideEffect: SE) {
        _sideEffects.offer(sideEffect)
    }

    fun dispatchViewState(viewState: VS) {
        _viewState.offer(viewState)
    }
}