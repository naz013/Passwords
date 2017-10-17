package com.cray.software.passwords.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.cray.software.passwords.cloud.Dropbox;
import com.cray.software.passwords.helpers.PostDataBase;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.utils.SuperUtil;

public class DelayedTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;

    public DelayedTask(Context context){
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        PostDataBase pdb = new PostDataBase(mContext);
        pdb.open();
        Dropbox dropbox = new Dropbox(mContext);
        if (dropbox.isLinked()) {
            Cursor c = pdb.fetchCodes();
            if (c != null && c.moveToFirst()) {
                do {
                    int db_code = c.getInt(c.getColumnIndex(PostDataBase.COLUMN_PROCESS));
                    long row_id = c.getLong(c.getColumnIndex(PostDataBase.COLUMN_ID));
                    if (db_code == Constants.FILE_DELETE) {
                        Cursor cx = pdb.fetchProcess(row_id);
                        if (cx != null && cx.moveToFirst()) {
                            String name = cx.getString(cx.getColumnIndex(PostDataBase.COLUMN_FILE_NAME));
                            dropbox.deleteFile(name);
                            pdb.deleteProcess(row_id);
                            cx.close();
                        }
                    } else if (db_code == Constants.AUTO_FILE) {
                        Cursor ac = pdb.fetchProcess(row_id);
                        if (ac != null && ac.moveToFirst()) {
                            String fileToUpload = ac.getString(ac.getColumnIndex(PostDataBase.COLUMN_FILE_NAME));
                            if (SuperUtil.isConnected(mContext)) {
                                dropbox.uploadToCloud(fileToUpload);
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
