package com.cray.software.passwords.modern_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.cray.software.passwords.utils.ThemeUtil

abstract class BaseFragment : Fragment() {

    protected var themeUtil: ThemeUtil? = null
    protected var anInterface: FragmentInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        themeUtil = ThemeUtil.getInstance(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    open fun onFragmentResume() {
        if (anInterface != null) {
            anInterface!!.setCurrent(this)
        }
    }

    fun onFragmentPause() {

    }

    fun canGoBack(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        onFragmentResume()
    }
}
