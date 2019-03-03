package com.cray.software.passwords.cloud

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.cray.software.passwords.R
import com.cray.software.passwords.interfaces.Module
import com.cray.software.passwords.utils.LogUtil

/**
 * Copyright 2017 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class DropboxLogin(context: AppCompatActivity, callback: LoginCallback) {

    companion object {
        const val TAG: String = "DropboxLogin"
        const val MARKET_APP_JUSTREMINDER: String = "com.cray.software.passwords"
        const val MARKET_APP_JUSTREMINDER_PRO: String = "com.cray.software.passwordspro"
    }

    private var mContext: AppCompatActivity = context
    private var mDropbox: Dropbox = Dropbox(context)
    private var mCallback: LoginCallback = callback

    init {
        mDropbox.startSession()
    }

    fun login() {
        var isIn = isAppInstalled(MARKET_APP_JUSTREMINDER_PRO)
        if (Module.isPro) isIn = isAppInstalled(MARKET_APP_JUSTREMINDER)
        if (isIn) {
            checkDialog().show()
        } else {
            performDropboxLinking()
        }
    }

    private fun performDropboxLinking() {
        if (mDropbox.isLinked) {
            if (mDropbox.unlink()) {
                mCallback.onSuccess(false)
            }
        } else {
            mDropbox.startLink()
        }
    }

    fun checkDropboxStatus() {
        LogUtil.d(TAG,  "checkDropboxStatus: " + mDropbox.isLinked)
        if (mDropbox.isLinked) {
            mCallback.onSuccess(true)
        } else {
            LogUtil.d(TAG,  "checkDropboxStatus2: " + mDropbox.isLinked)
            mDropbox.startSession()
            if (mDropbox.isLinked) {
                mCallback.onSuccess(true)
            } else {
                mCallback.onSuccess(false)
            }
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = mContext.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun checkDialog(): Dialog {
        return AlertDialog.Builder(mContext)
                .setMessage(mContext.getString(R.string.other_version_message))
                .setPositiveButton(mContext.getString(R.string.open_app_dialog_button), { _, _ ->
                    val i: Intent
                    val manager = mContext.packageManager
                    i = if (Module.isPro) {
                        manager.getLaunchIntentForPackage(MARKET_APP_JUSTREMINDER)
                    } else {
                        manager.getLaunchIntentForPackage(MARKET_APP_JUSTREMINDER_PRO)
                    }
                    i.addCategory(Intent.CATEGORY_LAUNCHER)
                    mContext.startActivity(i)
                })
                .setNegativeButton(mContext.getString(R.string.dialog_button_delete), { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    if (Module.isPro) {
                        intent.data = Uri.parse("package:" + MARKET_APP_JUSTREMINDER)
                    } else {
                        intent.data = Uri.parse("package:" + MARKET_APP_JUSTREMINDER_PRO)
                    }
                    mContext.startActivity(intent)
                })
                .setNeutralButton(mContext.getString(R.string.button_close), { dialog, _ -> dialog.dismiss() })
                .setCancelable(true)
                .create()
    }

    interface LoginCallback {
        fun onSuccess(logged: Boolean) {

        }
    }
}