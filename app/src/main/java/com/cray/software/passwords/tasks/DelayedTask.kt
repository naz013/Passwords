package com.cray.software.passwords.tasks

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask

import com.cray.software.passwords.cloud.Dropbox
import com.cray.software.passwords.helpers.PostDataBase
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.utils.SuperUtil

class DelayedTask(private val mContext: Context) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void): Boolean? {
        val pdb = PostDataBase(mContext)
        pdb.open()
        val dropbox = Dropbox(mContext)
        if (dropbox.isLinked) {
            val c = pdb.fetchCodes()
            if (c != null && c.moveToFirst()) {
                do {
                    val db_code = c.getInt(c.getColumnIndex(PostDataBase.COLUMN_PROCESS))
                    val row_id = c.getLong(c.getColumnIndex(PostDataBase.COLUMN_ID))
                    if (db_code == Constants.FILE_DELETE) {
                        val cx = pdb.fetchProcess(row_id)
                        if (cx != null && cx.moveToFirst()) {
                            val name = cx.getString(cx.getColumnIndex(PostDataBase.COLUMN_FILE_NAME))
                            dropbox.deleteFile(name)
                            pdb.deleteProcess(row_id)
                            cx.close()
                        }
                    } else if (db_code == Constants.AUTO_FILE) {
                        val ac = pdb.fetchProcess(row_id)
                        if (ac != null && ac.moveToFirst()) {
                            val fileToUpload = ac.getString(ac.getColumnIndex(PostDataBase.COLUMN_FILE_NAME))
                            if (SuperUtil.isConnected(mContext)) {
                                dropbox.uploadToCloud(fileToUpload)
                            }
                            pdb.deleteProcess(row_id)
                            ac.close()
                        }
                    }
                } while (c.moveToNext())
            }
        }
        pdb.close()
        return true
    }
}
