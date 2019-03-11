package com.cray.software.passwords.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject

abstract class ThemedActivity : AppCompatActivity() {

    protected val themeUtil: ThemeUtil by inject()
    protected val prefs: Prefs by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(themeUtil.style)
    }
}
