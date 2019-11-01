package com.regin.starving.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationWithZoom
import com.regin.starving.core.map.MapView
import com.regin.starving.core.map.Poi
import com.regin.starving.map.utils.VectorDrawableBitmapDescriptor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Suppress("EXPERIMENTAL_API_USAGE")
class MapFragment : Fragment(R.layout.fragment_map), MapView {

    private val markers = mutableSetOf<Marker>()

    private val googleMapFragment: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleMapFragment.getMapAsync {
            it.setPadding(0, 100, 0, 0)

            it.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoContents(marker: Marker): View {
                    val poi = marker.tag as Poi

                    return LayoutInflater.from(requireContext())
                        .inflate(R.layout.view_poi, view as ViewGroup, false)
                        .apply {
                            findViewById<TextView>(R.id.name).text = poi.name
                            findViewById<TextView>(R.id.address).text = poi.address
                        }
                }

                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }
            })
        }
    }

    override fun listenToMap(): Flow<LocationWithZoom> {
        return callbackFlow {
            googleMapFragment.getMapAsync { map ->
                val listener = GoogleMap.OnCameraMoveListener {
                    with(map.cameraPosition) {
                        offer(
                            LocationWithZoom(
                                location = Location(target.latitude, target.longitude),
                                zoom = zoom
                            )
                        )
                    }
                }

                map.setOnCameraMoveListener(listener)
            }
            awaitClose {
                googleMapFragment.getMapAsync { it.setOnCameraMoveListener(null) }
            }
        }
    }

    override fun listToPoiClick(): Flow<Poi> {
        return callbackFlow {
            googleMapFragment.getMapAsync {
                it.setOnInfoWindowClickListener { marker ->
                    offer(marker.tag as Poi)
                }
            }

            awaitClose {
                googleMapFragment.getMapAsync { it.setOnInfoWindowClickListener(null) }
            }
        }
    }

    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun drawPois(pois: List<Poi>) {
        googleMapFragment.getMapAsync { map ->
            coroutineScope.launch {
                for (poi in pois) {
                    val markerOptions = buildMarker(poi)
                    map.addMarker(markerOptions).apply {
                        tag = poi
                        markers.add(this)
                    }
                }

                markers.filterNot { it.isMarkerVisible(map) }
                    .forEach { it.remove() }
            }
        }
    }

    override fun focusOnUserLocation(location: Location) {
        googleMapFragment.getMapAsync {
            it.isMyLocationEnabled = true
            it.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude), 12.0f
                )
            )
        }
    }

    private fun buildMarker(poi: Poi): MarkerOptions {
        val latLng = LatLng(poi.location.latitude, poi.location.longitude)

        return MarkerOptions().apply {
            position(latLng)
            title(poi.name)
            icon(
                VectorDrawableBitmapDescriptor.decode(requireContext(), R.drawable.ic_restaurant)
            )
        }
    }

    private fun Marker.isMarkerVisible(map: GoogleMap) =
        map.projection.visibleRegion.latLngBounds.contains(position)

    companion object {
        private const val MARKERS_MAXIMUM_COUNT = 100
    }

}
