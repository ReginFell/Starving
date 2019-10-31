package com.regin.starving.location

import android.content.Context
import android.location.Location as PlatformLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnCompleteListener
import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationIsNotAvailableException
import com.regin.starving.core.location.LocationProvider
import com.regin.starving.core.location.LocationServiceIsNotAvailableException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleLocationProvider(context: Context) : LocationProvider {

    private val client = FusedLocationProviderClient(context)

    @Suppress("EXPERIMENTAL_API_USAGE")
    override suspend fun lastKnownLocation(): Location {
        return suspendCoroutine { continuation ->
            val onCompleteListener = OnCompleteListener<PlatformLocation?> { result ->
                if (result.isSuccessful) {
                    if (result.result == null) {
                        val locationRequest = LocationRequest.create()
                            .setNumUpdates(1)
                            .setInterval(UPDATE_INTERVAL_TIME)
                            .setMaxWaitTime(MAXIMUM_WAIT_TIME)

                        client.requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                                if (!locationAvailability.isLocationAvailable) {
                                    continuation.resumeWithException(LocationServiceIsNotAvailableException())
                                } else {
                                    continuation.resumeWithException(LocationIsNotAvailableException())
                                }
                            }

                            override fun onLocationResult(result: LocationResult) {
                                client.removeLocationUpdates(this)
                            }
                        }, null)

                    } else {
                        continuation.resume(
                            result.result!!.let {
                                Location(it.latitude, it.longitude)
                            }
                        )
                    }
                } else {
                    continuation.resumeWithException(LocationServiceIsNotAvailableException())
                }
            }

            client.lastLocation
                .addOnCompleteListener(onCompleteListener)
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_TIME = 10L
        private const val MAXIMUM_WAIT_TIME = 50L
    }
}
