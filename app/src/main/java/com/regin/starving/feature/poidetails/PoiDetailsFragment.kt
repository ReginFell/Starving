package com.regin.starving.feature.poidetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.regin.starving.R
import kotlinx.android.synthetic.main.fragment_poi_details.*

class PoiDetailsFragment : Fragment(R.layout.fragment_poi_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val poi = arguments?.let { PoiDetailsFragmentArgs.fromBundle(it) }!!.poi

        name.text = poi.name
        address.text = poi.address
    }
}