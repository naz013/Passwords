package com.cray.software.passwords

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.cray.software.passwords.helpers.SyncHelper
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.login.ActivityLogin
import com.cray.software.passwords.login.ActivitySignUp
import com.cray.software.passwords.utils.PrefsConstants
import com.cray.software.passwords.utils.ThemedActivity
import java.io.File

class SplashScreen : ThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            writePrefs()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        prefs.checkPrefs()
    }

    override fun onResume() {
        super.onResume()
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
                this.prefs.loadSharedPreferencesFromFile(prefs)
            } else {
                this.prefs.saveSharedPreferencesToFile(prefs)
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
        val settings = File("/data/data/" + packageName + "/shared_prefs/" + PrefsConstants.LOGIN_PREFS + ".xml")
        if (settings.exists()) {
            val loadedStr = prefs.isPassString
            if (loadedStr) {
                attachSingle()
            } else {
                attachDouble()
            }
        } else {
            val appSettings = getSharedPreferences(PrefsConstants.LOGIN_PREFS, Context.MODE_PRIVATE)
            val ed = appSettings.edit()
            ed.apply()
            attachDouble()
        }
    }
}
