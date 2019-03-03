package com.cray.software.passwords.cloud

import android.content.Context
import android.os.Environment
import android.widget.Toast

import com.cray.software.passwords.R
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.utils.LogUtil
import com.cray.software.passwords.utils.Prefs
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.files.Metadata
import com.dropbox.core.v2.files.WriteMode
import com.dropbox.core.v2.users.FullAccount
import com.dropbox.core.v2.users.SpaceUsage

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

import okhttp3.OkHttpClient

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

class Dropbox(private val mContext: Context) {

    private var mDBApi: DbxClientV2? = null

    /**
     * Check if user has already connected to Dropbox from this application.
     *
     * @return Boolean
     */
    val isLinked: Boolean
        get() = mDBApi != null && Prefs.getInstance(mContext).dropboxToken != null

    /**
     * Start connection to Dropbox.
     */
    fun startSession() {
        var token = Prefs.getInstance(mContext).dropboxToken
        if (token == null) {
            token = Auth.getOAuth2Token()
            Prefs.getInstance(mContext).dropboxToken = token
        }
        LogUtil.d(TAG, "startSession: " + token!!)
        if (token == null) {
            Prefs.getInstance(mContext).dropboxToken = null
            return
        }
        val requestConfig = DbxRequestConfig.newBuilder("Just Reminder")
                .withHttpRequestor(OkHttp3Requestor(OkHttpClient()))
                .build()

        mDBApi = DbxClientV2(requestConfig, token)
    }

    /**
     * Holder Dropbox user name.
     *
     * @return String user name
     */
    fun userName(): String? {
        var account: FullAccount? = null
        try {
            account = mDBApi!!.users().currentAccount
        } catch (e: DbxException) {
            e.printStackTrace()
        }

        return account?.name?.displayName
    }

    /**
     * Holder user all apace on Dropbox.
     *
     * @return Long - user quota
     */
    fun userQuota(): Long {
        var account: SpaceUsage? = null
        try {
            account = mDBApi!!.users().spaceUsage
        } catch (e: DbxException) {
            LogUtil.e(TAG, "userQuota: ", e)
        }

        return account?.allocation?.individualValue?.allocated ?: 0
    }

    fun userQuotaNormal(): Long {
        var account: SpaceUsage? = null
        try {
            account = mDBApi!!.users().spaceUsage
        } catch (e: DbxException) {
            LogUtil.e(TAG, "userQuotaNormal: ", e)
        }

        return account?.used ?: 0
    }

    fun startLink() {
        try {
            Auth.startOAuth2Authentication(mContext, APP_KEY)
        } catch (e: IllegalStateException) {
            Toast.makeText(mContext, R.string.dropbox_app_not_installed, Toast.LENGTH_SHORT).show()
        }

    }

    fun unlink(): Boolean {
        var `is` = false
        if (logOut()) {
            `is` = true
        }
        return `is`
    }

    private fun logOut(): Boolean {
        clearKeys()
        return true
    }

    private fun clearKeys() {
        Prefs.getInstance(mContext).dropboxToken = null
        Prefs.getInstance(mContext).dropboxUid = null
    }

    /**
     * Upload to Dropbox folder backup files from selected folder on SD Card.
     */
    fun uploadToCloud(fileName: String?) {
        startSession()
        if (!isLinked) {
            return
        }
        if (fileName != null) {
            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
            if (sdPathDr.exists()) {
                val fileLoc = sdPathDr.toString()
                val tmpFile = File(fileLoc, fileName)
                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(tmpFile)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

                try {
                    val filePath = "/" + Constants.DIR_DBX + tmpFile.name
                    mDBApi!!.files().uploadBuilder(filePath)
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(fis)
                } catch (e: DbxException) {
                    LogUtil.e(TAG, "Something went wrong while uploading.", e)
                } catch (e: IOException) {
                    LogUtil.e(TAG, "Something went wrong while uploading.", e)
                }

            }
        } else {
            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
            val files = sdPathDr.listFiles()
            val fileLoc = sdPathDr.toString()
            if (files == null) {
                return
            }
            for (file in files) {
                val fileLoopName = file.name
                val tmpFile = File(fileLoc, fileLoopName)
                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(tmpFile)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

                try {
                    val filePath = "/" + Constants.DIR_DBX + fileLoopName
                    mDBApi!!.files().uploadBuilder(filePath)
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(fis)
                } catch (e: DbxException) {
                    LogUtil.e(TAG, "Something went wrong while uploading.", e)
                } catch (e: IOException) {
                    LogUtil.e(TAG, "Something went wrong while uploading.", e)
                }

            }
        }
    }

    /**
     * Delete reminder backup file from Dropbox folder.
     *
     * @param name file name.
     */
    fun deleteFile(name: String) {
        LogUtil.d(TAG, "deleteFile: $name")
        startSession()
        if (!isLinked) {
            return
        }
        try {
            mDBApi!!.files().delete("/" + Constants.DIR_DBX + name)
        } catch (e: DbxException) {
            LogUtil.e(TAG, "deleteFile: ", e)
        }

    }

    /**
     * Delete all folders inside application folder on Dropbox.
     */
    fun cleanFolder() {
        startSession()
        if (!isLinked) {
            return
        }
        deleteFolder("/" + Constants.DIR_DBX)
    }

    private fun deleteFolder(folder: String) {
        try {
            mDBApi!!.files().delete(folder)
        } catch (e: DbxException) {
            LogUtil.e(TAG, "deleteFolder: ", e)
        }

    }

    /**
     * Download on SD Card all template backup files found on Dropbox.
     */
    fun downloadFiles() {
        val sdPath = Environment.getExternalStorageDirectory()
        val dir = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP)
        startSession()
        if (!isLinked) {
            return
        }
        try {
            val result = mDBApi!!.files().listFolder("/" + Constants.DIR_DBX) ?: return
            for (e in result.entries) {
                val fileName = e.name
                val localFile = File("$dir/$fileName")
                val cloudFile = "/" + Constants.DIR_DBX + fileName
                downloadFile(localFile, cloudFile)
            }
        } catch (e: DbxException) {
            LogUtil.e(TAG, "downloadFiles: ", e)
        } catch (e: IllegalStateException) {
            LogUtil.e(TAG, "downloadFiles: ", e)
        }

    }

    private fun downloadFile(localFile: File, cloudFile: String) {
        try {
            if (!localFile.exists()) {
                localFile.createNewFile()
            }
            val outputStream = FileOutputStream(localFile)
            mDBApi!!.files().download(cloudFile).download(outputStream)
        } catch (e1: DbxException) {
            LogUtil.e(TAG, "downloadFile: ", e1)
        } catch (e1: IOException) {
            LogUtil.e(TAG, "downloadFile: ", e1)
        }

    }

    /**
     * Count all reminder backup files in Dropbox folder.
     *
     * @return number of found backup files.
     */
    fun countFiles(): Int {
        val count = 0
        startSession()
        if (!isLinked) {
            return 0
        }
        try {
            val result = mDBApi!!.files().listFolder("/") ?: return 0
        } catch (e: DbxException) {
            LogUtil.e(TAG, "countFiles: ", e)
        }

        return count
    }

    companion object {

        private val TAG = "Dropbox"
        private val APP_KEY = "4zi1d414h0v8sxe"
    }
}
