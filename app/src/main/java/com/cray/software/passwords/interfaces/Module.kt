package com.cray.software.passwords.interfaces

import android.os.Build

import com.cray.software.passwords.BuildConfig

object Module {
    val isPro: Boolean
        get() = BuildConfig.IS_PRO

    val isLollipop: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}
