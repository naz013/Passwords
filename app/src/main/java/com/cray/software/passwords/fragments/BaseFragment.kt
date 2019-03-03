package com.cray.software.passwords.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View

import com.cray.software.passwords.utils.ThemeUtil

/**
 * Copyright 2017 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
abstract class BaseFragment : Fragment() {

    protected var themeUtil: ThemeUtil? = null

    protected var anInterface: FragmentInterface? = null

    protected abstract val bgView: View?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil = ThemeUtil.getInstance(context)
    }

    private fun setBackground() {
        if (bgView != null && themeUtil != null) {
            bgView!!.setBackgroundColor(themeUtil!!.backgroundStyle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackground()
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (anInterface == null) {
            anInterface = context as FragmentInterface?
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (anInterface == null) {
            anInterface = activity as FragmentInterface?
        }
    }
}
