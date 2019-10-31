package com.regin.starving.core.map

import com.regin.starving.core.location.Location

data class Poi(
    val id: String,
    val name: String,
    val location: Location,
    val address: String?
)