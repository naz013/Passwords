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
import com.cray.software.passwords.passwords.Password;
import com.cray.software.passwords.utils.Prefs;
import com.cray.software.passwords.utils.SuperUtil;

import java.io.File;

public class DeleteTask extends AsyncTask<Long, Void, Boolean> {

    private Context mContext;
    private SyncListener mListener;

    public DeleteTask(Context context, SyncListener mListener){
        this.mContext = context;
        this.mListener = mListener;
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        if (params.length > 0) {
            long id = params[0];
            long delBackup = 0;
            if (params.length > 1) delBackup = params[1];
            Password password = DataProvider.getPassword(mContext, id);
            DataProvider.deletePassword(mContext, password);
            if (delBackup == 1) {
                if (Prefs.getInstance(mContext).isDeleteBackFileEnabled()) {
                    File sdPath = Environment.getExternalStorageDirectory();
                    File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD + "/" + password.getUuId() + Constants.FILE_EXTENSION);
                    if (sdPathDr.exists()) {
                        sdPathDr.delete();
                    }
                    File sdPathTmp = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP + "/" + password.getUuId() + Constants.FILE_EXTENSION);
                    if (sdPathTmp.exists()) {
                        sdPathTmp.delete();
                    }
                    boolean isConnected = SuperUtil.isConnected(mContext);
                    String dbxFile = ("/" + Constants.DIR_DBX + password.getUuId() + Constants.FILE_EXTENSION);
                    Dropbox dbx = new Dropbox(mContext);
                    if (dbx.isLinked()) {
                        if (isConnected) {
                            dbx.deleteFile(password.getUuId());
                        } else {
                            PostDataBase postDataBase = new PostDataBase(mContext);
                            postDataBase.addProcess(password.getUuId(), dbxFile, Constants.FILE_DELETE);
                            postDataBase.close();
                        }
                    }

                    File sdPathGd = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP + "/" + password.getUuId() + Constants.FILE_EXTENSION);
                    if (sdPathGd.exists()) {
                        sdPathGd.delete();
                    }

                    Google google = Google.getInstance(mContext);
                    if (isConnected && google != null && google.getDrive() != null) {
                        google.getDrive().deleteFile(password.getUuId());
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
