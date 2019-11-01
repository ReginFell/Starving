package com.regin.starving.core.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(val latitude: Double, val longitude: Double) : Parcelable