package com.cray.software.passwords.notes

import android.content.Context
import android.os.AsyncTask
import android.os.Environment

import com.cray.software.passwords.cloud.Dropbox
import com.cray.software.passwords.cloud.Google
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.utils.SuperUtil

import java.io.File

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

class DeleteNoteFilesAsync(private val mContext: Context) : AsyncTask<String, Void, Void>() {

    override fun doInBackground(vararg params: String): Void? {
        for (uid in params) {
            val exportFileName = uid + Constants.FILE_EXTENSION_NOTE
            val sdPath = Environment.getExternalStorageDirectory()
            val sdPathDr = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD)
            var file = File(sdPathDr, exportFileName)
            if (file.exists()) file.delete()
            val sdPathD = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP)
            file = File(sdPathD, exportFileName)
            if (file.exists()) file.delete()
            val sdPathG = File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP)
            file = File(sdPathG, exportFileName)
            if (file.exists()) file.delete()
            val isConnected = SuperUtil.isConnected(mContext)
            if (isConnected) {
                Dropbox(mContext).deleteFile(exportFileName)
                val google = Google.getInstance(mContext)
                if (google != null && google.drive != null) {
                    google.drive!!.deleteFile(exportFileName)
                }
            }
        }
        return null
    }
}
