package com.regin.starving.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class PoiResponse(val response: Response)

@Serializable
data class Response(val venues: List<VenueResponse>)