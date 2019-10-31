package com.regin.starving.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class VenueResponse(val id: String, val name: String, val location: VenueLocation)

@Serializable
data class VenueLocation(val lat: Double, val lng: Double, val address: String? = null)