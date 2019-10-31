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
import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationWithZoom
import com.regin.starving.core.map.MapView
import com.regin.starving.core.map.Poi
import com.regin.starving.map.utils.VectorDrawableBitmapDescriptor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.maps.android.clustering.ClusterManager


@Suppress("EXPERIMENTAL_API_USAGE")
class MapFragment : Fragment(R.layout.fragment_map), MapView {

    private val googleMapFragment: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.mapView, SupportMapFragment.newInstance())
                .commitNow()
        }

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

    override fun listenMap(): Flow<LocationWithZoom> {
        return callbackFlow {
            googleMapFragment.getMapAsync { map ->
                val listener = GoogleMap.OnCameraMoveListener {
                    with(map.cameraPosition) {
                        offer(
                            LocationWithZoom(
                                location = Location(
                                    target.latitude,
                                    target.longitude
                                ),
                                zoom = zoom
                            )
                        )
                    }
                }

                map.setOnCameraMoveListener(listener)
            }
            awaitClose {}
        }
    }

    override fun drawPois(pois: List<Poi>) {
        googleMapFragment.getMapAsync {
            it.clear()

            val visiblePois = pois.filter { poi ->
                val latLng = LatLng(poi.location.latitude, poi.location.longitude)
                it.isMarkerVisible(latLng)
            }

            val poisToRender =
                if (visiblePois.size <= MAXIMUM_MARKERS) visiblePois else visiblePois.subList(
                    0,
                    MAXIMUM_MARKERS
                )

            for (poi in poisToRender) {
                val latLng = LatLng(poi.location.latitude, poi.location.longitude)

                if (it.isMarkerVisible(latLng)) {
                    val markerOptions = MarkerOptions()

                    markerOptions.position(latLng)
                    markerOptions.title(poi.name)
                    markerOptions.icon(
                        VectorDrawableBitmapDescriptor.decode(
                            requireContext(),
                            R.drawable.ic_restaurant
                        )
                    )

                    val marker = it.addMarker(markerOptions)
                    marker.tag = poi
                }
            }
        }
    }

    override fun focusOnUserLocation(location: Location) {
        googleMapFragment.getMapAsync {
            it.isMyLocationEnabled = true
            it.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), 12.0f
                )
            )
        }
    }

    private fun GoogleMap.isMarkerVisible(markerPosition: LatLng) =
        projection.visibleRegion.latLngBounds.contains(markerPosition)

    companion object {
        private const val MAXIMUM_MARKERS = 200
    }
}
