package com.regin.starving.feature.poidetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.regin.starving.R
import com.regin.starving.core.map.Poi
import com.regin.starving.core.utils.IntentUtils
import com.regin.starving.core.utils.setToolbar
import kotlinx.android.synthetic.main.fragment_poi_details.*

class PoiDetailsFragment : Fragment(R.layout.fragment_poi_details) {

    private val poi: Poi by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.let { PoiDetailsFragmentArgs.fromBundle(it) }?.poi
            ?: throw IllegalArgumentException()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(toolbar)
        toolbar.setupWithNavController(view.findNavController())

        toolbar.title = getString(R.string.poi_details)

        name.text = poi.name
        address.text = poi.address

        openDirection.setOnClickListener {
            IntentUtils.openMap(requireContext(), poi.location.latitude, poi.location.longitude)
        }
    }
}