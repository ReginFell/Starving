package com.regin.starving.feature.explore

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.google.android.material.snackbar.Snackbar
import com.regin.starving.R
import com.regin.starving.core.map.MapView
import com.regin.starving.core.ui.BaseFragment
import com.regin.starving.feature.poidetails.PoiDetailsFragmentArgs
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@UseExperimental(FlowPreview::class)
class ExploreFragment : BaseFragment(R.layout.fragment_explore) {

    private val viewModel by viewModel<ExploreViewModel>()

    private val mapView: MapView by lazy(LazyThreadSafetyMode.NONE) {
        childFragmentManager.findFragmentById(R.id.map) as MapView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(exploreModule)

        loadMyLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findLocation.setOnClickListener {
            loadMyLocation()
        }
    }

    override fun onResume() {
        super.onResume()

        observeViewState()
        observeSideEffects()

        mapView.listenCameraChanges()
            .debounce(TimeUnit.SECONDS.toMillis(1))
            .onEach { viewModel.dispatchEvent(Event.LoadPoi(it)) }
            .launchIn(viewScope)

        mapView.listenPoiClicks()
            .debounce(TimeUnit.MILLISECONDS.toMillis(250))
            .onEach {
                val directions = PoiDetailsFragmentArgs(it).toBundle()
                findNavController(this).navigate(
                    R.id.action_exploreFragment_to_poiDetailsFragment,
                    directions
                )
            }
            .launchIn(viewScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(exploreModule)
    }

    private fun observeViewState() {
        viewModel.viewState
            .onEach { render(it) }
            .launchIn(viewScope)
    }

    private fun observeSideEffects() {
        viewModel.sideEffects
            .onEach { react(it) }
            .launchIn(viewScope)
    }

    private fun loadMyLocation() {
        lifecycleScope.launchWhenCreated {
            try {
                askPermission()
                viewModel.dispatchEvent(Event.LoadMyLocation)
            } catch (e: PermissionException) {
                Toast.makeText(
                    requireContext(),
                    R.string.location_permission_declined_message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun render(viewState: ExploreViewState) {
        if (viewState.isLoading) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
        mapView.drawPois(viewState.pois.toList())
    }

    private fun react(sideEffect: SideEffect) {
        when (sideEffect) {
            is SideEffect.FocusOnMyLocation -> mapView.focusOnUserLocation(sideEffect.location)
            is SideEffect.LocationServiceIsUnavailable -> {
                Snackbar.make(
                    view as ViewGroup,
                    R.string.cant_detect_location,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.check_settings) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
            }
            is SideEffect.LocationIsUnknown -> {
                Snackbar.make(container, R.string.location_is_unknown, Snackbar.LENGTH_LONG)
                    .show()
            }
            is SideEffect.FailedLoadingPoi -> {
                Snackbar.make(container, sideEffect.reason, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}