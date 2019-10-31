package com.regin.starving.domain.usecase

import com.regin.starving.core.location.Location
import com.regin.starving.core.map.Poi
import com.regin.starving.data.api.CategoryId
import com.regin.starving.data.repository.PoiRepository
import kotlin.math.abs

class LoadRestaurantsUseCase(private val poiRepository: PoiRepository) {

    companion object {
        private const val ZOOM_TO_RADIUS_MULTIPLIER = 250
    }

    suspend fun loadRestaurants(location: Location, zoom: Float = 10f): List<Poi> {
        return poiRepository.loadPoi(
            location,
            calculateRadius(zoom),
            listOf(CategoryId.RESTARAUNT)
        )
            .map {
                Poi(
                    id = it.id,
                    name = it.name,
                    location = Location(it.location.lat, it.location.lng),
                    address = it.location.address
                )
            }
    }

    private fun calculateRadius(zoomLevel: Float): Double {
        return (abs(20 - zoomLevel) * ZOOM_TO_RADIUS_MULTIPLIER).toDouble()
    }
}