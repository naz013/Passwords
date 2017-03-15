package com.cray.software.passwords.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cray.software.passwords.R;
import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.cloud.GDriveHelper;
import com.cray.software.passwords.helpers.DataProvider;
import com.cray.software.passwords.helpers.PostDataBase;
import com.cray.software.passwords.helpers.SharedPrefs;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.SyncListener;
import com.cray.software.passwords.notes.NoteItem;

import java.io.File;

public class DeleteNoteTask extends AsyncTask<Long, Void, Boolean> {

    private Context mContext;
    private SyncListener mListener;
    private ProgressDialog pd;

    public DeleteNoteTask(Context context, SyncListener mListener){
        this.mContext = context;
        this.mListener = mListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER);
        pd.setMessage(mContext.getString(R.string.deleting_note));
        pd.setCancelable(false);
        pd.setIndeterminate(false);
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        if (params.length > 0) {
            long del = params[0];
            NoteItem noteItem = DataProvider.getNote(mContext, del);
            DataProvider.deleteNote(mContext, noteItem);
            SharedPrefs sPrefs = new SharedPrefs(mContext);
            DropboxHelper dbx = new DropboxHelper(mContext);
            if (sPrefs.loadBoolean(Constants.NEW_PREFERENCES_CHECKBOX)) {
                File sdPath = Environment.getExternalStorageDirectory();
                File sdPathDr = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD + "/" + noteItem.getKey() + Constants.FILE_EXTENSION);
                if (sdPathDr.exists()) {
                    sdPathDr.delete();
                }
                File sdPathTmp = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_DBX_TMP + "/" + noteItem.getKey() + Constants.FILE_EXTENSION);
                if (sdPathTmp.exists()) {
                    sdPathTmp.delete();
                }
                boolean isConnected = SyncHelper.isConnected(mContext);
                String dbxFile = ("/" + Constants.DIR_DBX + noteItem.getKey() + Constants.FILE_EXTENSION);
                if (dbx.isLinked()) {
                    if (isConnected) {
                        dbx.deleteFile(noteItem.getKey());
                    } else {
                        PostDataBase postDataBase = new PostDataBase(mContext);
                        postDataBase.addProcess(noteItem.getKey(), dbxFile, Constants.FILE_DELETE);
                        postDataBase.close();
                    }
                }

                File sdPathGd = new File(sdPath.toString() + "/Pass_backup/" + Constants.DIR_SD_GDX_TMP + "/" + noteItem.getKey() + Constants.FILE_EXTENSION);
                if (sdPathGd.exists()) {
                    sdPathGd.delete();
                }

                GDriveHelper gdx = new GDriveHelper(mContext);
                if (gdx.isLinked()){
                    if (isConnected) {
                        gdx.deleteFile(noteItem.getKey());
                    }
                }
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
        if (mListener != null) {
            mListener.endExecution(aVoid);
        }
    }
}
