package com.cray.software.passwords.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Copyright 2016 Nazar Suhovich
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

object SuperUtil {

    private val TAG = "SuperUtil"

    fun stopService(context: Context, clazz: Class<*>) {
        context.stopService(Intent(context, clazz))
    }

    fun getString(fragment: Fragment, id: Int): String {
        return if (fragment.isAdded) {
            fragment.getString(id)
        } else
            ""
    }

    fun isGooglePlayServicesAvailable(a: AppCompatActivity): Boolean {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(a)
        return resultCode == ConnectionResult.SUCCESS
    }

    fun checkGooglePlayServicesAvailability(a: AppCompatActivity): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(a)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(a, result, 69).show()
            }
            return false
        } else {
            return true
        }
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun appendString(vararg strings: String): String {
        val stringBuilder = StringBuilder()
        for (string in strings) {
            if (string != null) {
                stringBuilder.append(string)
            }
        }
        return stringBuilder.toString()
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        var installed: Boolean
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            installed = false
        }

        return installed
    }

    fun decrypt(string: String?): String {
        if (string == null) return ""
        var result = ""
        val byte_string = Base64.decode(string, Base64.DEFAULT)
        try {
            result = String(byte_string, Charset.forName("UTF-8"))
        } catch (e1: UnsupportedEncodingException) {
            e1.printStackTrace()
        }

        return result
    }

    fun encrypt(string: String?): String {
        if (string == null) return ""
        var string_byted: ByteArray? = null
        try {
            string_byted = string.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return Base64.encodeToString(string_byted, Base64.DEFAULT).trim { it <= ' ' }
    }

    fun launchMarket(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Failed to launch Play Store", Toast.LENGTH_SHORT).show()
        }

    }
}
