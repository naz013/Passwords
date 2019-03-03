package com.cray.software.passwords.cloud

import android.accounts.Account
import android.accounts.AccountManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity

import com.cray.software.passwords.R
import com.cray.software.passwords.utils.Prefs
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.common.AccountPicker
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager
import com.google.api.services.drive.DriveScopes

import java.io.IOException

import androidx.appcompat.app.AppCompatActivity.RESULT_OK

/**
 * Copyright 2017 Nazar Suhovich
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

class GoogleLogin(private val activity: AppCompatActivity, private val mCallback: LoginCallback?) {

    private var mGoogle: Google? = null
    private var mAccountName: String? = null

    val isLogged: Boolean
        get() = mGoogle != null

    init {
        mGoogle = Google.getInstance(activity)
        if (mGoogle != null) mCallback.onSuccess()
    }

    fun logOut() {
        Prefs.getInstance(activity).driveUser = Prefs.DRIVE_USER_NONE
        mGoogle!!.logOut()
        mGoogle = null
    }

    fun login() {
        val intent = AccountPicker.newChooseAccountIntent(null, null,
                arrayOf("com.google"), false, null, null, null, null)
        activity.startActivityForResult(intent, REQUEST_AUTHORIZATION)
    }

    private fun getAndUseAuthTokenInAsyncTask(account: Account) {
        val task = object : AsyncTask<Account, String, String>() {
            internal var progressDlg: ProgressDialog? = null
            internal var me: AsyncTask<Account, String, String> = this

            override fun onPreExecute() {
                progressDlg = ProgressDialog(activity, ProgressDialog.STYLE_SPINNER)
                progressDlg!!.max = 100
                progressDlg!!.setMessage(activity.getString(R.string.wait_message))
                progressDlg!!.setCancelable(false)
                progressDlg!!.isIndeterminate = false
                progressDlg!!.setOnCancelListener { dialog ->
                    progressDlg!!.dismiss()
                    me.cancel(true)
                }
                progressDlg!!.show()
            }

            override fun doInBackground(vararg params: Account): String? {
                return getAccessToken(params[0])
            }

            override fun onPostExecute(s: String?) {
                if (s != null) {
                    mAccountName = s
                }
                try {
                    if (progressDlg != null && progressDlg!!.isShowing) {
                        progressDlg!!.dismiss()
                    }
                } catch (e: IllegalArgumentException) {

                }

                mCallback?.onSuccess()
            }
        }
        task.execute(account)
    }

    private fun getAccessToken(account: Account): String? {
        try {
            val scope = "oauth2:" + DriveScopes.DRIVE
            return GoogleAuthUtil.getToken(activity, account, scope)
        } catch (e: UserRecoverableAuthException) {
            activity.startActivityForResult(e.intent, REQUEST_ACCOUNT_PICKER)
            e.printStackTrace()
            return null
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: GoogleAuthException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            val gam = GoogleAccountManager(activity)
            getAndUseAuthTokenInAsyncTask(gam.getAccountByName(mAccountName))
            finishLogin()
        } else if (requestCode == REQUEST_ACCOUNT_PICKER && resultCode == Activity.RESULT_OK) {
            mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            finishLogin()
            mCallback?.onSuccess()
        } else {
            mCallback?.onFail()
        }
    }

    private fun finishLogin() {
        Prefs.getInstance(activity).driveUser = mAccountName
        mGoogle = Google.getInstance(activity)
    }

    interface LoginCallback {
        fun onSuccess()

        fun onFail()
    }

    companion object {

        private val TAG = "GoogleLogin"
        private val REQUEST_AUTHORIZATION = 1
        private val REQUEST_ACCOUNT_PICKER = 3
    }
}
