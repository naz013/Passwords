package com.cray.software.passwords.utils

import android.content.Context
import android.content.SharedPreferences
import java.io.*

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

abstract class SharedPrefs(context: Context) : PrefsConstants() {

    private val prefs: SharedPreferences = context.getSharedPreferences(PrefsConstants.PREFS_NAME, Context.MODE_PRIVATE)
    protected var prefsPassword: SharedPreferences = context.getSharedPreferences(PrefsConstants.LOGIN_PREFS, Context.MODE_PRIVATE)


    fun putString(stringToSave: String, value: String) {
        prefs.edit().putString(stringToSave, value).apply()
    }

    fun putInt(stringToSave: String, value: Int) {
        prefs.edit().putInt(stringToSave, value).apply()
    }

    fun getInt(stringToLoad: String): Int {
        var x: Int
        try {
            x = prefs.getInt(stringToLoad, 0)
        } catch (e: ClassCastException) {
            x = 0
        }

        return x
    }

    fun putLong(stringToSave: String, value: Long) {
        prefs.edit().putLong(stringToSave, value).apply()
    }

    fun getLong(stringToLoad: String): Long {
        var x: Long
        try {
            x = prefs.getLong(stringToLoad, 1000)
        } catch (e: ClassCastException) {
            x = 1000
        }

        return x
    }

    fun getString(stringToLoad: String, def: String): String {
        return prefs.getString(stringToLoad, def) ?: def
    }

    fun getString(stringToLoad: String): String {
        return prefs.getString(stringToLoad, "") ?: ""
    }

    fun hasKey(checkString: String): Boolean {
        return prefs.contains(checkString)
    }

    fun putBoolean(stringToSave: String, value: Boolean) {
        prefs.edit().putBoolean(stringToSave, value).apply()
    }

    fun getBoolean(stringToLoad: String): Boolean {
        var res: Boolean
        try {
            res = prefs.getBoolean(stringToLoad, false)
        } catch (e: ClassCastException) {
            res = false
        }

        return res
    }

    fun saveSharedPreferencesToFile(dst: File): Boolean {
        var res = false
        var output: ObjectOutputStream? = null
        try {
            output = ObjectOutputStream(FileOutputStream(dst))
            output.writeObject(prefs.all)
            res = true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (output != null) {
                    output.flush()
                    output.close()
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
        return res
    }

    fun loadSharedPreferencesFromFile(src: File): Boolean {
        var res = false
        var input: ObjectInputStream? = null
        try {
            input = ObjectInputStream(FileInputStream(src))
            val prefEdit = prefs.edit()
            prefEdit.clear()
            val entries = input.readObject() as Map<String, *>
            for ((key, v) in entries) {

                if (v is Boolean)
                    prefEdit.putBoolean(key, v)
                else if (v is Float)
                    prefEdit.putFloat(key, v)
                else if (v is Int)
                    prefEdit.putInt(key, v)
                else if (v is Long)
                    prefEdit.putLong(key, v)
                else if (v is String)
                    prefEdit.putString(key, v)
            }
            prefEdit.apply()
            res = true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                input?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
        return res
    }

    companion object {

        private val TAG = "SharedPrefs"
    }
}