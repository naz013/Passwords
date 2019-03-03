package com.cray.software.passwords

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity

import com.cray.software.passwords.helpers.SyncHelper
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.login.ActivityLogin
import com.cray.software.passwords.login.ActivitySignUp
import com.cray.software.passwords.utils.Prefs

import java.io.File

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            writePrefs()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Prefs.getInstance(this).checkPrefs()
        checkKeys()
    }

    private fun writePrefs() {
        val isSD = SyncHelper.isSdPresent
        if (isSD) {
            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.PREFS)
            if (!sdPathDr.exists()) {
                sdPathDr.mkdirs()
            }
            val prefs = File("$sdPathDr/prefs.xml")
            if (prefs.exists()) {
                Prefs.getInstance(this).loadSharedPreferencesFromFile(prefs)
            } else {
                Prefs.getInstance(this).saveSharedPreferencesToFile(prefs)
            }
        }
    }

    private fun attachDouble() {
        startActivity(Intent(this, ActivitySignUp::class.java))
        finish()
    }

    private fun attachSingle() {
        startActivity(Intent(this, ActivityLogin::class.java))
        finish()
    }

    private fun checkKeys() {
        val settings = File("/data/data/" + packageName + "/shared_prefs/" + Prefs.LOGIN_PREFS + ".xml")
        if (settings.exists()) {
            val loadedStr = Prefs.getInstance(this).isPassString
            if (loadedStr) {
                attachSingle()
            } else {
                attachDouble()
            }
        } else {
            val appSettings = getSharedPreferences(Prefs.LOGIN_PREFS, Context.MODE_PRIVATE)
            val ed = appSettings.edit()
            ed.apply()
            attachDouble()
        }
    }
}
