package com.regin.starving.feature.explore

import androidx.lifecycle.viewModelScope
import com.regin.starving.core.async.DispatcherHolder
import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationIsNotAvailableException
import com.regin.starving.core.location.LocationProvider
import com.regin.starving.core.location.LocationServiceIsNotAvailableException
import com.regin.starving.core.location.LocationWithZoom
import com.regin.starving.core.map.Poi
import com.regin.starving.core.viewmodel.StatefulViewModel
import com.regin.starving.domain.usecase.LoadRestaurantsUseCase
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExploreViewModel(
    private val dispatchers: DispatcherHolder,
    private val locationProvider: LocationProvider,
    private val loadRestaurantsUseCase: LoadRestaurantsUseCase
) : StatefulViewModel<ExploreViewState, Action, SideEffect>() {

    override val defaultViewState: ExploreViewState
        get() = ExploreViewState(pois = setOf(), isLoading = false)

    init {
        viewModelScope.launch {
            actions.consumeEach {
                when (it) {
                    is Action.LoadMyLocation -> loadMyLocation()
                    is Action.LoadPoi -> loadPoi(it)
                    is Action.OnPoiLoaded -> onPoiLoaded(it)
                }
            }
        }
    }

    private fun onPoiLoaded(action: Action.OnPoiLoaded) {
        val pois = mutableSetOf<Poi>().apply {
            addAll(currentState.pois)
            addAll(action.pois)
        }

        dispatchViewState(currentState.copy(pois = pois, isLoading = false))
    }

    private suspend fun loadMyLocation() {
        coroutineScope {
            try {
                val location = locationProvider.lastKnownLocation()
                dispatchSideEffect(SideEffect.FocusOnMyLocation(location))
            } catch (e: Exception) {
                when (e) {
                    is LocationIsNotAvailableException -> dispatchSideEffect(SideEffect.LocationIsUnknown)
                    is LocationServiceIsNotAvailableException -> dispatchSideEffect(SideEffect.LocationServiceIsUnavailable)
                }

            }
        }
    }

    private suspend fun loadPoi(action: Action.LoadPoi) {
        coroutineScope {
            dispatchViewState(currentState.copy(isLoading = true))
            try {
                val result = withContext(dispatchers.IO) {
                    loadRestaurantsUseCase.loadRestaurants(
                        action.locationWithRadius.location,
                        action.locationWithRadius.zoom
                    )
                }
                dispatchAction(Action.OnPoiLoaded(result))
            } catch (e: Exception) {
                dispatchViewState(currentState.copy(isLoading = false))
                dispatchSideEffect(SideEffect.FailedLoadingPoi(e.message.toString()))
            }
        }
    }
}

sealed class Action {
    object LoadMyLocation : Action()
    data class LoadPoi(val locationWithRadius: LocationWithZoom) : Action()
    class OnPoiLoaded(internal val pois: List<Poi>) : Action()
}

sealed class SideEffect {
    data class FocusOnMyLocation(val location: Location) : SideEffect()
    object LocationServiceIsUnavailable : SideEffect()
    object LocationIsUnknown : SideEffect()
    class FailedLoadingPoi(val reason: String) : SideEffect()
}
