package com.cray.software.passwords.views

import android.view.View
import androidx.annotation.IdRes
import com.cray.software.passwords.utils.lazyUnSynchronized

abstract class Binding(val view: View) {

    fun <ViewT : View> bindView(@IdRes idRes: Int): Lazy<ViewT> {
        return lazyUnSynchronized {
            view.findViewById<ViewT>(idRes)
        }
    }
}