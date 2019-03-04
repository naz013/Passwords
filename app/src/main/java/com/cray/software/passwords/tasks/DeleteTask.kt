package com.cray.software.passwords.tasks

import android.content.Context
import android.os.AsyncTask
import android.os.Environment

import com.cray.software.passwords.cloud.Dropbox
import com.cray.software.passwords.cloud.Google
import com.cray.software.passwords.helpers.DataProvider
import com.cray.software.passwords.helpers.PostDataBase
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.interfaces.SyncListener
import com.cray.software.passwords.passwords.Password
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.SuperUtil

import java.io.File

class DeleteTask(private val mContext: Context, private val mListener: SyncListener?) : AsyncTask<Long, Void, Boolean>() {

    protected override fun doInBackground(vararg params: Long): Boolean? {
        if (params.size > 0) {
            val id = params[0]
            var delBackup = 0L
            if (params.size > 1) delBackup = params[1]
            val password = DataProvider.getPassword(mContext, id)
            DataProvider.deletePassword(mContext, password!!)
            if (delBackup == 1L) {
                val sdPath = Environment.getExternalStorageDirectory()
                val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD + "/" + password.uuId + Constants.FILE_EXTENSION)
                if (sdPathDr.exists()) {
                    sdPathDr.delete()
                }
                val sdPathTmp = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP + "/" + password.uuId + Constants.FILE_EXTENSION)
                if (sdPathTmp.exists()) {
                    sdPathTmp.delete()
                }
                val isConnected = SuperUtil.isConnected(mContext)
                val dbxFile = "/" + Constants.DIR_DBX + password.uuId + Constants.FILE_EXTENSION
                val dbx = Dropbox(mContext)
                if (dbx.isLinked) {
                    if (isConnected) {
                        dbx.deleteFile(password.uuId)
                    } else {
                        val postDataBase = PostDataBase(mContext)
                        postDataBase.addProcess(password.uuId, dbxFile, Constants.FILE_DELETE)
                        postDataBase.close()
                    }
                }

                val sdPathGd = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP + "/" + password.uuId + Constants.FILE_EXTENSION)
                if (sdPathGd.exists()) {
                    sdPathGd.delete()
                }

                val google = Google.getInstance(mContext)
                if (isConnected && google != null && google.drive != null) {
                    google.drive!!.deleteFile(password.uuId)
                }
            }
        }
        return true
    }

    override fun onPostExecute(aVoid: Boolean?) {
        super.onPostExecute(aVoid)
        mListener?.endExecution(aVoid!!)
    }
}