package com.regin.starving.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentUtils {

    fun openMap(context: Context, lat: Double, lng: Double) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?saddr=$lat,$lng")
        )
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}