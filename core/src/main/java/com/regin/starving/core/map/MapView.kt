package com.regin.starving.core.map

import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationWithZoom
import kotlinx.coroutines.flow.Flow

interface MapView {

    fun focusOnUserLocation(location: Location)

    fun drawPois(pois: List<Poi>)

    fun listenMap(): Flow<LocationWithZoom>

}