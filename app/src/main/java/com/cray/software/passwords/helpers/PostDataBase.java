package com.cray.software.passwords.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class PostDataBase {
    public static final String DB_NAME = "post_process";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "dbx_process";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_PROCESS = "process_code";
    public static final String COLUMN_PATH = "file_path";
    public static final String COLUMN_TECH = "first_tech";
    public static final String COLUMN_SECOND_TECH = "second_tech";
    private DBHelper dbHelper;
    static Context mContext;
    private SQLiteDatabase db;

    private static final String DB_CREATE =
            "create table " + TABLE_NAME + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_FILE_NAME + " VARCHAR(255), " +
                    COLUMN_PROCESS + " integer, " +
                    COLUMN_PATH + " VARCHAR(255), " +
                    COLUMN_TECH + " VARCHAR(255), " +
                    COLUMN_SECOND_TECH + " VARCHAR(255)" +
                    ");";

    public class DBHelper extends SQLiteOpenHelper {


        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }


    }

    public PostDataBase(Context c) {
        mContext = c;
    }

    public PostDataBase open() throws SQLiteException {
        dbHelper = new DBHelper(mContext);

        db = dbHelper.getWritableDatabase();

        System.gc();
        return this;
    }

    public boolean isOpen () {
        return db != null && db.isOpen();
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

    public void close() {
        if( dbHelper != null )
            dbHelper.close();
    }

    public long addProcess (String file, String path, int code) {
        openGuard();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FILE_NAME, file);
        cv.put(COLUMN_PATH, path);
        cv.put(COLUMN_PROCESS, code);
        //cv.put(COLUMN_TECH, ul);
        //cv.put(COLUMN_SECOND_TECH, cm);
        //Log.d(LOG_TAG, "data is inserted " + cv);
        return db.insert(TABLE_NAME, null, cv);
    }

    public boolean deleteProcess(long rowId) {
        openGuard();
        return db.delete(TABLE_NAME, COLUMN_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchProcess(long rowId) throws SQLException {
        openGuard();
        return db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_FILE_NAME, COLUMN_PATH, COLUMN_PROCESS, COLUMN_TECH, COLUMN_SECOND_TECH}, COLUMN_ID  + "=" + rowId, null, null, null, null, null);
    }

    public Cursor fetchCodes() throws SQLException {
        openGuard();
        return db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_PROCESS}, null, null, null, null, null);
    }

    public int getCountPass() throws SQLException {
        openGuard();
        String countQuery = "SELECT " + COLUMN_FILE_NAME + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void openGuard() throws SQLiteException {
        if(isOpen()) return;
        open();
        if(isOpen()) return;
        throw new SQLiteException("Could not open database");
    }
}
