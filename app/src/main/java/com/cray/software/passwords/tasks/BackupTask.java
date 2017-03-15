package com.cray.software.passwords.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.cloud.GDriveHelper;
import com.cray.software.passwords.helpers.SyncHelper;

import org.json.JSONException;

import java.io.IOException;

public class BackupTask extends AsyncTask<Void, Void, Boolean> {

    private Context tContext;

    public BackupTask(Context context){
        this.tContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        SyncHelper sHelp = new SyncHelper(tContext);
        try {
            sHelp.exportPasswords();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        boolean isConnected = SyncHelper.isConnected(tContext);
        if (isConnected) {
            new DropboxHelper(tContext).uploadToCloud(null);
        }

        if (isConnected) {
            try {
                new GDriveHelper(tContext).saveFileToDrive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
