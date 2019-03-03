package com.cray.software.passwords.tasks

import android.content.Context
import android.os.AsyncTask

import com.cray.software.passwords.cloud.Dropbox
import com.cray.software.passwords.cloud.Google
import com.cray.software.passwords.helpers.SyncHelper
import com.cray.software.passwords.utils.SuperUtil

import org.json.JSONException

import java.io.IOException

class BackupTask(private val mContext: Context) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void): Boolean? {
        val sHelp = SyncHelper(mContext)
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
            val google = Google.getInstance(mContext)
            if (google != null && google.drive != null) {
                try {
                    google.drive!!.saveFileToDrive()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            val dropbox = Dropbox(mContext)
            if (dropbox.isLinked) dropbox.uploadToCloud(null)
        }
        return true
    }

}
