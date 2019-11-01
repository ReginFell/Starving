package com.regin.starving.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class PoiClusterItem(
    private val title: String,
    private val snippet: String,
    private val position: LatLng
) : ClusterItem {


    override fun getSnippet(): String = snippet

    override fun getTitle(): String = title

    override fun getPosition(): LatLng = position

}