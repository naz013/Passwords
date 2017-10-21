package com.cray.software.passwords.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.cray.software.passwords.cloud.Dropbox;
import com.cray.software.passwords.cloud.Google;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.utils.SuperUtil;

import org.json.JSONException;

import java.io.IOException;

public class BackupTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;

    public BackupTask(Context context){
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SyncHelper sHelp = new SyncHelper(mContext);
        try {
            sHelp.exportPasswords();
            sHelp.exportNotes();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        boolean isConnected = SuperUtil.isConnected(mContext);
        if (isConnected) {
            Google google = Google.getInstance(mContext);
            if (google != null && google.getDrive() != null) {
                try {
                    google.getDrive().saveFileToDrive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Dropbox dropbox = new Dropbox(mContext);
            if (dropbox.isLinked()) dropbox.uploadToCloud(null);
        }
        return true;
    }

}
