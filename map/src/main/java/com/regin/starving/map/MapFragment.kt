package com.regin.starving.map

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationWithZoom
import com.regin.starving.core.map.MapView
import com.regin.starving.core.map.Poi
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.regin.starving.map.utils.VectorDrawableBitmapDescriptor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@Suppress("EXPERIMENTAL_API_USAGE")
class MapFragment : Fragment(R.layout.fragment_map), MapView {

    private val googleMapFragment: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
    }

    private var clusterManager: ClusterManager<PoiClusterItem>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleMapFragment.getMapAsync {
            it.setPadding(0, 100, 0, 0)

            if (clusterManager == null) {
                clusterManager = ClusterManager<PoiClusterItem>(requireContext(), it).apply {
                    renderer = DefaultClusterRenderer<PoiClusterItem>(requireContext(), it, this)
                    algorithm = NonHierarchicalDistanceBasedAlgorithm<PoiClusterItem>()
                }
            }
        }
    }

    override fun listenCameraChanges(): Flow<LocationWithZoom> {
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

    override fun listenPoiClicks(): Flow<Poi> {
        return callbackFlow {
            googleMapFragment.getMapAsync {
                clusterManager?.setOnClusterItemInfoWindowClickListener { clusterItem ->
                    offer(clusterItem.poi)
                }
                it.setOnInfoWindowClickListener(clusterManager)
                it.setOnMarkerClickListener(clusterManager)
            }

            awaitClose {
                clusterManager?.setOnClusterItemInfoWindowClickListener(null)
            }
        }
    }

    override fun drawPois(pois: List<Poi>) {
        val markersOptions = pois.map { buildMarker(it) to it }
        setupClusters(markersOptions)
    }

    override fun focusOnUserLocation(location: Location) {
        googleMapFragment.getMapAsync {
            it.isMyLocationEnabled = true
            it.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude), INITIAL_USER_CAMERA_ZOOM
                )
            )
        }
    }

    private fun setupClusters(listMarkers: List<Pair<MarkerOptions, Poi>>) {
        for ((markerOptions, poi) in listMarkers) {
            val clusterItem = PoiClusterItem(
                title = markerOptions.title,
                position = markerOptions.position,
                poi = poi
            )
            clusterManager?.addItem(clusterItem)
            clusterManager?.cluster()
        }
    }

    private fun buildMarker(poi: Poi): MarkerOptions {
        val latLng = LatLng(poi.location.latitude, poi.location.longitude)

        return MarkerOptions().apply {
            position(latLng)
            title(poi.name)
            icon(
                VectorDrawableBitmapDescriptor.decode(
                    requireContext(),
                    R.drawable.ic_restaurant
                )
            )
        }
    }

    companion object {
        private const val INITIAL_USER_CAMERA_ZOOM = 14f
    }
}