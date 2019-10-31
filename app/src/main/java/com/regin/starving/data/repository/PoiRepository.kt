package com.regin.starving.data.repository

import com.regin.starving.core.location.Location
import com.regin.starving.data.api.Api
import com.regin.starving.data.api.response.VenueResponse

open class PoiRepository(private val api: Api) {

    suspend fun loadPoi(
        location: Location,
        radius: Double,
        categoryIds: List<String>
    ): List<VenueResponse> {
        return api.loadPoi("${location.latitude}, ${location.longitude}", radius, categoryIds)
            .response.venues
    }
}