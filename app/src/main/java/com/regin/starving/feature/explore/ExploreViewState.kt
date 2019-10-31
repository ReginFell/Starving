package com.regin.starving.feature.explore

import com.regin.starving.core.map.Poi

data class ExploreViewState(
    val pois: Set<Poi>,
    val isLoading: Boolean
)