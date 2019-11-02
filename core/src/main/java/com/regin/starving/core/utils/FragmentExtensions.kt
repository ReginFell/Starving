package com.regin.starving.core.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

fun Fragment.setToolbar(toolbar: Toolbar) {
    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
}