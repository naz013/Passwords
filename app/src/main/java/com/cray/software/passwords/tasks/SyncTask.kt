package com.cray.software.passwords.tasks

import android.annotation.TargetApi
import android.content.Context
import android.os.AsyncTask
import android.os.Build

import com.cray.software.passwords.cloud.Dropbox
import com.cray.software.passwords.cloud.Google
import com.cray.software.passwords.helpers.SyncHelper
import com.cray.software.passwords.interfaces.SyncListener
import com.cray.software.passwords.utils.SuperUtil

import org.json.JSONException

import java.io.IOException

class SyncTask(private val mContext: Context, private val mListener: SyncListener?) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void): Boolean? {
        val sHelp = SyncHelper(mContext)
        val dbx = Dropbox(mContext)
        try {
            sHelp.exportPasswords()
            sHelp.exportNotes()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val isConnected = SuperUtil.isConnected(mContext)
        if (isConnected) {
            dbx.uploadToCloud(null)
            dbx.downloadFiles()
        }
        val google = Google.getInstance(mContext)
        if (isConnected && google != null && google.drive != null) {
            try {
                google.drive!!.saveFileToDrive()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                google.drive!!.loadFileFromDrive()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        try {
            sHelp.importObjectsFromJson()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return true
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onPostExecute(aVoid: Boolean?) {
        super.onPostExecute(aVoid)
        mListener?.endExecution(aVoid!!)
    }
}
