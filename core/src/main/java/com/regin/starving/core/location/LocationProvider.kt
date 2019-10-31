package com.regin.starving.core.location

interface LocationProvider {

    suspend fun lastKnownLocation(): Location

}