package com.cray.software.passwords.tasks;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.cray.software.passwords.cloud.Dropbox;
import com.cray.software.passwords.cloud.Google;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.SyncListener;
import com.cray.software.passwords.utils.SuperUtil;

import org.json.JSONException;

import java.io.IOException;

public class SyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private SyncListener mListener;

    public SyncTask(Context context, SyncListener mListener){
        this.mContext = context;
        this.mListener = mListener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SyncHelper sHelp = new SyncHelper(mContext);
        Dropbox dbx = new Dropbox(mContext);
        try {
            sHelp.exportPasswords();
            sHelp.exportNotes();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        boolean isConnected = SuperUtil.isConnected(mContext);
        if (isConnected) {
            dbx.uploadToCloud(null);
            dbx.downloadFiles();
        }
        Google google = Google.getInstance(mContext);
        if (isConnected && google != null && google.getDrive() != null) {
            try {
                google.getDrive().saveFileToDrive();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                google.getDrive().loadFileFromDrive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            sHelp.importObjectsFromJson();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (mListener != null) {
            mListener.endExecution(aVoid);
        }
    }
}
