package com.cray.software.passwords.tasks;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.Dropbox;
import com.cray.software.passwords.cloud.Google;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.interfaces.SyncListener;
import com.cray.software.passwords.utils.SuperUtil;

import org.json.JSONException;

import java.io.IOException;

public class SyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private NotificationManager mNotifyMgr;
    private Notification.Builder builder;
    private SyncListener mListener;

    public SyncTask(Context context, SyncListener mListener){
        this.mContext = context;
        builder = new Notification.Builder(context);
        this.mListener = mListener;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        builder.setContentTitle(mContext.getString(R.string.sync_start_message));
        builder.setContentText(mContext.getString(R.string.wait_message));
        builder.setSmallIcon(R.drawable.ic_sync_white_24dp);
        mNotifyMgr =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(2, builder.build());
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
        builder.setContentTitle(mContext.getString(R.string.sync_finished_message));
        builder.setSmallIcon(R.drawable.ic_done_white_24dp);
        if (Module.isPro()){
            builder.setContentText(mContext.getString(R.string.app_name));
        } else {
            builder.setContentText(mContext.getString(R.string.app_name_free));
        }
        builder.setWhen(System.currentTimeMillis());
        mNotifyMgr.notify(2, builder.build());
        if (mListener != null) {
            mListener.endExecution(aVoid);
        }
    }
}
