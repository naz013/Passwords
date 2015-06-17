package com.cray.software.passwords.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.cray.software.passwords.cloud.DropboxHelper;
import com.cray.software.passwords.helpers.PostDataBase;
import com.cray.software.passwords.helpers.SyncHelper;
import com.cray.software.passwords.interfaces.Constants;

public class DelayedTask extends AsyncTask<Void, Void, Boolean> {

    Context tContext;
    PostDataBase pdb;
    DropboxHelper dbx;

    public DelayedTask(Context context){
        this.tContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        pdb = new PostDataBase(tContext);
        pdb.open();
        dbx = new DropboxHelper(tContext);
        if (dbx.isLinked()) {
            Cursor c = pdb.fetchCodes();
            if (c != null && c.moveToFirst()) {
                do {
                    int db_code = c.getInt(c.getColumnIndex(PostDataBase.COLUMN_PROCESS));
                    long row_id = c.getLong(c.getColumnIndex(PostDataBase.COLUMN_ID));
                    if (db_code == Constants.FILE_DELETE) {
                        Cursor cx = pdb.fetchProcess(row_id);
                        if (cx != null && cx.moveToFirst()) {
                            String name = cx.getString(cx.getColumnIndex(PostDataBase.COLUMN_FILE_NAME));
                            dbx.deleteFile(name);
                            pdb.deleteProcess(row_id);
                            cx.close();
                        }
                    } else if (db_code == Constants.AUTO_FILE) {
                        Cursor ac = pdb.fetchProcess(row_id);
                        if (ac != null && ac.moveToFirst()) {
                            String fileToUpload = ac.getString(ac.getColumnIndex(PostDataBase.COLUMN_FILE_NAME));
                            if (SyncHelper.isConnected(tContext)) {
                                new DropboxHelper(tContext).uploadToCloud(fileToUpload);
                            }
                            pdb.deleteProcess(row_id);
                            ac.close();
                        }
                    }
                } while (c.moveToNext());
            }
        }
        pdb.close();
        return true;
    }
}
