package com.cray.software.passwords.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cray.software.passwords.cloud.Dropbox;
import com.cray.software.passwords.cloud.Google;
import com.cray.software.passwords.helpers.DataProvider;
import com.cray.software.passwords.helpers.PostDataBase;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.SyncListener;
import com.cray.software.passwords.notes.NoteItem;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

import java.io.File;

public class DeleteNoteTask extends AsyncTask<Long, Void, Boolean> {

    private Context mContext;
    private SyncListener mListener;

    public DeleteNoteTask(Context context, SyncListener mListener) {
        this.mContext = context;
        this.mListener = mListener;
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        if (params.length > 0) {
            long id = params[0];
            long delBackup = 0;
            if (params.length > 1) delBackup = params[1];
            NoteItem noteItem = DataProvider.getNote(mContext, id);
            DataProvider.deleteNote(mContext, noteItem);
            if (delBackup == 1) {
                Dropbox dbx = new Dropbox(mContext);
                if (Prefs.getInstance(mContext).isDeleteBackFileEnabled()) {
                    File sdPath = Environment.getExternalStorageDirectory();
                    File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD + "/" + noteItem.getKey() + Constants.FILE_EXTENSION_NOTE);
                    if (sdPathDr.exists()) {
                        sdPathDr.delete();
                    }
                    File sdPathTmp = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP + "/" + noteItem.getKey() + Constants.FILE_EXTENSION_NOTE);
                    if (sdPathTmp.exists()) {
                        sdPathTmp.delete();
                    }
                    boolean isConnected = SuperUtil.isConnected(mContext);
                    String dbxFile = ("/" + Constants.DIR_DBX + noteItem.getKey() + Constants.FILE_EXTENSION_NOTE);
                    if (dbx.isLinked()) {
                        if (isConnected) {
                            dbx.deleteFile(noteItem.getKey());
                        } else {
                            PostDataBase postDataBase = new PostDataBase(mContext);
                            postDataBase.addProcess(noteItem.getKey(), dbxFile, Constants.FILE_DELETE);
                            postDataBase.close();
                        }
                    }

                    File sdPathGd = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP + "/" + noteItem.getKey() + Constants.FILE_EXTENSION_NOTE);
                    if (sdPathGd.exists()) {
                        sdPathGd.delete();
                    }

                    Google google = Google.getInstance(mContext);
                    if (isConnected && google != null && google.getDrive() != null) {
                        google.getDrive().deleteFile(noteItem.getKey());
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (mListener != null) {
            mListener.endExecution(aVoid);
        }
    }
}
