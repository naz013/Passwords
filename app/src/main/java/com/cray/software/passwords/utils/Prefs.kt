package com.cray.software.passwords.utils

import android.content.Context

import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.interfaces.Module

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

class Prefs(context: Context) : SharedPrefs(context) {

    var restoreWord: String?
        get() = prefsPassword.getString(PrefsConstants.KEYWORD, null)
        set(value) = prefsPassword.edit().putString(PrefsConstants.KEYWORD, value).apply()

    val isPassString: Boolean
        get() = prefsPassword.contains(PrefsConstants.LOGIN_PASSCODE)

    var driveUser: String
        get() = SuperUtil.decrypt(getString(PrefsConstants.DRIVE_USER))
        set(value) = putString(PrefsConstants.DRIVE_USER, SuperUtil.encrypt(value))

    var isRateShowed: Boolean
        get() = getBoolean(PrefsConstants.RATE_SHOW)
        set(value) = putBoolean(PrefsConstants.RATE_SHOW, value)

    var isDeleteBackFileEnabled: Boolean
        get() = getBoolean(PrefsConstants.DELETE_BACKUP)
        set(value) = putBoolean(PrefsConstants.DELETE_BACKUP, value)

    var isAutoBackupEnabled: Boolean
        get() = getBoolean(PrefsConstants.AUTO_BACKUP)
        set(value) = putBoolean(PrefsConstants.AUTO_BACKUP, value)

    var isAutoSyncEnabled: Boolean
        get() = getBoolean(PrefsConstants.AUTO_SYNC)
        set(value) = putBoolean(PrefsConstants.AUTO_SYNC, value)

//    var appThemeColor: Int
//        get() = getInt(PrefsConstants.APP_THEME_COLOR)
//        set(value) = putInt(PrefsConstants.APP_THEME_COLOR, value)

    var appTheme: Int
        get() = getInt(PrefsConstants.APP_THEME)
        set(value) = putInt(PrefsConstants.APP_THEME, value)

    var passwordLength: Int
        get() = getInt(PrefsConstants.PASSWORD_LENGTH)
        set(value) = putInt(PrefsConstants.PASSWORD_LENGTH, value)

    var oldPasswordLength: Int
        get() = getInt(PrefsConstants.PASSWORD_OLD_LENGTH)
        set(value) = putInt(PrefsConstants.PASSWORD_OLD_LENGTH, value)

    var orderBy: String
        get() = getString(PrefsConstants.ORDER_BY, Constants.ORDER_DATE_A_Z)
        set(value) = putString(PrefsConstants.ORDER_BY, value)

    var runsCount: Int
        get() = getInt(PrefsConstants.APP_RUNS_COUNT)
        set(value) = putInt(PrefsConstants.APP_RUNS_COUNT, value)

    var dropboxUid: String
        get() = getString(PrefsConstants.DROPBOX_UID)
        set(uid) = putString(PrefsConstants.DROPBOX_UID, uid)

    var dropboxToken: String
        get() = getString(PrefsConstants.DROPBOX_TOKEN)
        set(token) = putString(PrefsConstants.DROPBOX_TOKEN, token)

    fun hasSecureKey(checkString: String): Boolean {
        return prefsPassword.contains(checkString)
    }

    fun savePassPrefs(value: String) {
        prefsPassword.edit().putString(PrefsConstants.LOGIN_PASSCODE, value).apply()
    }

    fun loadPassPrefs(): String {
        return prefsPassword.getString(PrefsConstants.LOGIN_PASSCODE, "1111") ?: "1111"
    }

    fun saveSystemPrefs(key: String, value: String) {
        prefsPassword.edit().putString(key, value).apply()
    }

    fun loadSystemPrefs(key: String): String? {
        return prefsPassword.getString(key, Constants.DRIVE_USER_NONE)
    }

    fun isSystemKey(key: String): Boolean {
        return prefsPassword.contains(key)
    }

    fun checkPrefs() {
        if (!hasKey(PrefsConstants.APP_THEME_COLOR)) {
            putInt(PrefsConstants.APP_THEME_COLOR, 3)
        }
        if (!hasKey(PrefsConstants.APP_THEME)) {
            putInt(PrefsConstants.APP_THEME, ThemeUtil.THEME_AUTO)
        }
        if (!hasKey(PrefsConstants.PASSWORD_LENGTH)) {
            putInt(PrefsConstants.PASSWORD_LENGTH, 4)
        }
        if (!hasKey(PrefsConstants.DELETE_BACKUP)) {
            putBoolean(PrefsConstants.DELETE_BACKUP, false)
        }
        if (Module.isPro) {
            if (!hasKey(PrefsConstants.AUTO_SYNC)) {
                putBoolean(PrefsConstants.AUTO_SYNC, false)
            }
            if (!hasKey(PrefsConstants.AUTO_BACKUP)) {
                putBoolean(PrefsConstants.AUTO_BACKUP, false)
            }
        }
    }

    companion object {
        val DRIVE_USER_NONE = "none"
    }
}
