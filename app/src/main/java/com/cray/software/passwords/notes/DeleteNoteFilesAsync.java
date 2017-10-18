package com.cray.software.passwords.notes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cray.software.passwords.cloud.Dropbox;
import com.cray.software.passwords.cloud.Google;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.utils.SuperUtil;

import java.io.File;

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

public class DeleteNoteFilesAsync extends AsyncTask<String, Void, Void> {

    private Context mContext;

    public DeleteNoteFilesAsync(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        for (String uid : params) {
            String exportFileName = uid + Constants.FILE_EXTENSION_NOTE;
            File sdPath = Environment.getExternalStorageDirectory();
            File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD);
            File file = new File(sdPathDr, exportFileName);
            if (file.exists()) file.delete();
            File sdPathD = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP);
            file = new File(sdPathD, exportFileName);
            if (file.exists()) file.delete();
            File sdPathG = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP);
            file = new File(sdPathG, exportFileName);
            if (file.exists()) file.delete();
            boolean isConnected = SuperUtil.isConnected(mContext);
            if (isConnected) {
                new Dropbox(mContext).deleteFile(exportFileName);
                Google google = Google.getInstance(mContext);
                if (google != null && google.getDrive() != null) {
                    google.getDrive().deleteFile(exportFileName);
                }
            }
        }
        return null;
    }
}
