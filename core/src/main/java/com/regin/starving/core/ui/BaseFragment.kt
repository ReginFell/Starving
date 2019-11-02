package com.regin.starving.core.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {

    lateinit var viewScope: CoroutineScope

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewScope = CoroutineScope(Dispatchers.Main)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewScope.cancel()
    }
}