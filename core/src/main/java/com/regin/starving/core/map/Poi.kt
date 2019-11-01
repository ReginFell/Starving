package com.regin.starving.core.map

import android.os.Parcelable
import com.regin.starving.core.location.Location
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Poi(
    val id: String,
    val name: String,
    val location: Location,
    val address: String?
) : Parcelable