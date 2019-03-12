package com.cray.software.passwords

import android.app.Application
import com.cray.software.passwords.utils.components
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class PasswordsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin(this, components())
    }
}