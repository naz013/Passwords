package com.cray.software.passwords.tasks;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.cloud.GDriveHelper;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.ModuleManager;
import com.cray.software.passwords.interfaces.SyncListener;

import org.json.JSONException;

import java.io.IOException;

public class SyncTask extends AsyncTask<Void, Void, Boolean> {

    Context tContext;
    NotificationManager mNotifyMgr;
    Notification.Builder builder;
    DataBase DB;
    private SyncListener mListener;

    public SyncTask(Context context, SyncListener mListener){
        this.tContext = context;
        builder = new Notification.Builder(context);
        this.mListener = mListener;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        builder.setContentTitle(tContext.getString(R.string.sync_start_message));
        builder.setContentText(tContext.getString(R.string.wait_message));
        builder.setSmallIcon(R.drawable.ic_sync_white_24dp);
        mNotifyMgr =
                (NotificationManager) tContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(2, builder.build());
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DB = new DataBase(tContext);
        DB.open();
        SyncHelper sHelp = new SyncHelper(tContext);
        DropboxHelper dbx = new DropboxHelper(tContext);
        GDriveHelper gdx = new GDriveHelper(tContext);
        if (DB.getCountPass() > 0) {
            try {
                sHelp.exportPasswords();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        boolean isConnected = SyncHelper.isConnected(tContext);
        if (isConnected) {
            dbx.uploadToCloud(null);
            dbx.downloadFromCloud();
        }
        if (isConnected) {
            try {
                gdx.saveFileToDrive();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                gdx.loadFileFromDrive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            sHelp.importPasswordFromJSON();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        DB.close();

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        builder.setContentTitle(tContext.getString(R.string.sync_finished_message));
        builder.setSmallIcon(R.drawable.ic_done_white_24dp);
        if (new ModuleManager().isPro()){
            builder.setContentText(tContext.getString(R.string.app_name));
        } else builder.setContentText(tContext.getString(R.string.app_name_free));
        builder.setWhen(System.currentTimeMillis());
        mNotifyMgr.notify(2, builder.build());
        if (mListener != null) {
            mListener.EndExecution(aVoid);
        }
    }
}
