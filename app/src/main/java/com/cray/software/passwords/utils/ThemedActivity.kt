package com.cray.software.passwords.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.cray.software.passwords.interfaces.Module

abstract class ThemedActivity : AppCompatActivity() {

    protected var themeUtil: ThemeUtil? = null
        private set
    protected var prefs: Prefs? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs.getInstance(this)
        themeUtil = ThemeUtil.getInstance(this)
        if (themeUtil != null) setTheme(themeUtil!!.style)
        if (Module.isLollipop) {
            window.statusBarColor = themeUtil!!.getColor(themeUtil!!.colorPrimaryDark())
        }
    }
}
