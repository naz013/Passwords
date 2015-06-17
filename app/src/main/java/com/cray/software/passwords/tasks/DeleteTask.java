package com.cray.software.passwords.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.cloud.GDriveHelper;
import com.cray.software.passwords.helpers.DataBase;
import com.cray.software.passwords.helpers.PostDataBase;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.SyncListener;

import java.io.File;

public class DeleteTask extends AsyncTask<Long, Void, Boolean> {

    Context tContext;
    DataBase DB;
    private SyncListener mListener;
    PostDataBase postDataBase;
    DropboxHelper dbx;
    GDriveHelper gdx;
    ProgressDialog pd;

    public DeleteTask(Context context, SyncListener mListener){
        this.tContext = context;
        this.mListener = mListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(tContext, ProgressDialog.STYLE_SPINNER);
        pd.setMessage(tContext.getString(R.string.delete_password_message));
        pd.setCancelable(false);
        pd.setIndeterminate(false);
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        if (params.length > 0) {
            long del = params[0];
            DB = new DataBase(tContext);
            DB.open();
            Cursor c = DB.fetchPass(del);
            String title = null;
            if (c != null && c.moveToFirst()){
                title = c.getString(c.getColumnIndex(Constants.COLUMN_PIC_SEL));
            }
            SharedPrefs sPrefs = new SharedPrefs(tContext);
            dbx = new DropboxHelper(tContext);
            DB.deletePass(del);
            if (sPrefs.loadBoolean(Constants.NEW_PREFERENCES_CHECKBOX)) {
                File sdPath = Environment.getExternalStorageDirectory();
                File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD + "/" + title + Constants.FILE_EXTENSION);
                if (sdPathDr.exists()) {
                    sdPathDr.delete();
                }
                File sdPathTmp = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP + "/" + title + Constants.FILE_EXTENSION);
                if (sdPathTmp.exists()) {
                    sdPathTmp.delete();
                }
                boolean isConnected = SyncHelper.isConnected(tContext);
                String dbxFile = ("/" + Constants.DIR_DBX + title + Constants.FILE_EXTENSION);
                if (dbx.isLinked()) {
                    if (isConnected) {
                        dbx.deleteFile(title);
                    } else {
                        postDataBase = new PostDataBase(tContext);
                        postDataBase.addProcess(title, dbxFile, Constants.FILE_DELETE);
                        postDataBase.close();
                    }
                }

                File sdPathGd = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP + "/" + title + Constants.FILE_EXTENSION);
                if (sdPathGd.exists()) {
                    sdPathGd.delete();
                }

                gdx = new GDriveHelper(tContext);
                if (gdx.isLinked()){
                    if (isConnected) {
                        gdx.deleteFile(title);
                    }
                }
            }
            if (c != null) {
                c.close();
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (pd != null && pd.isShowing()){
            pd.dismiss();
        }
        DB.close();
        if (mListener != null) mListener.EndExecution(aVoid);
        Toast.makeText(tContext, tContext.getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
    }
}
