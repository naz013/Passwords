package com.cray.software.passwords

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.cray.software.passwords.utils.utilModule
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import timber.log.Timber

@Suppress("unused")
class PasswordsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Fabric.with(this, Crashlytics())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val logger = object : Logger(level = Level.DEBUG) {
            override fun log(level: Level, msg: MESSAGE) {
            }
        }
        startKoin{
            logger(logger)
            androidContext(this@PasswordsApp)
            modules(listOf(utilModule()))
        }
    }
}