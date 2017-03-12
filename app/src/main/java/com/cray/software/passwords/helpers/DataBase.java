package com.cray.software.passwords.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.cray.software.passwords.interfaces.Constants;

public class DataBase {
    public static final String DB_NAME = "appdb";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "passtab";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title_enter";
    public static final String COLUMN_LOGIN = "login_enter";
    public static final String COLUMN_PASSWORD = "password_enter";
    public static final String COLUMN_URL = "link_enter";
    public static final String COLUMN_COMMENT = "comment_enter";
    public static final String COLUMN_DATE = "date_enter";
    public static final String COLUMN_EDIT_TIMES = "edit_count";
    public static final String COLUMN_PIC_SEL = "selected_pic";
    public static final String COLUMN_TECHNICAL = "technical_col";
    private DBHelper dbHelper;
    static Context mContext;
    private SQLiteDatabase db;

    private static final String DB_CREATE =
            "create table " + TABLE_NAME + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " VARCHAR(255), " +
                    COLUMN_LOGIN + " VARCHAR(255), " +
                    COLUMN_PASSWORD + " VARCHAR(255), " +
                    COLUMN_URL + " VARCHAR(255), " +
                    COLUMN_COMMENT + " text, " +
                    COLUMN_DATE + " VARCHAR(255), " +
                    COLUMN_TECHNICAL + " integer, " +
                    COLUMN_PIC_SEL + " VARCHAR(255), " +
                    COLUMN_EDIT_TIMES + " integer" +
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

    public DataBase(Context c) {
        mContext = c;
    }

    public DataBase open() throws SQLiteException {
        dbHelper = new DBHelper(mContext);
        db = dbHelper.getWritableDatabase();
        System.gc();
        return this;
    }

    public boolean isOpen() {
        return db != null && db.isOpen();
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

    public void close() {
        if (dbHelper != null) dbHelper.close();
    }

    public long insertPass(Password password) {
        openGuard();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, password.getTitle());
        cv.put(COLUMN_LOGIN, password.getLogin());
        cv.put(COLUMN_PASSWORD, password.getPassword());
        cv.put(COLUMN_URL, password.getUrl());
        cv.put(COLUMN_COMMENT, password.getComment());
        cv.put(COLUMN_DATE, password.getDate());
        cv.put(COLUMN_TECHNICAL, password.getColor());
        cv.put(COLUMN_PIC_SEL, password.getUuId());
        return db.insert(TABLE_NAME, null, cv);
    }

    public boolean updatePass(Password password) {
        openGuard();
        ContentValues args = new ContentValues();
        args.put(COLUMN_TITLE, password.getTitle());
        args.put(COLUMN_LOGIN, password.getLogin());
        args.put(COLUMN_PASSWORD, password.getPassword());
        args.put(COLUMN_URL, password.getUrl());
        args.put(COLUMN_COMMENT, password.getComment());
        args.put(COLUMN_DATE, password.getDate());
        args.put(COLUMN_TECHNICAL, password.getColor());
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + password.getId(), null) > 0;
    }

    public boolean deletePass(long rowId) {
        openGuard();
        return db.delete(TABLE_NAME, COLUMN_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllPasswords() throws SQLException {
        openGuard();
        SharedPrefs prefs = new SharedPrefs(mContext);
        String orderPrefs = prefs.loadPrefs(Constants.NEW_PREFERENCES_ORDER_BY);
        String order = null;
        if (orderPrefs.matches(Constants.ORDER_DATE_A_Z)) {
            order = COLUMN_DATE + " ASC";
        } else if (orderPrefs.matches(Constants.ORDER_DATE_Z_A)) {
            order = COLUMN_DATE + " DESC";
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_A_Z)) {
            order = COLUMN_TITLE + " ASC";
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_Z_A)) {
            order = COLUMN_TITLE + " DESC";
        }
        return db.query(TABLE_NAME, null, null, null, null, null, order);
    }

    public boolean setUniqueId(long rowId, String id) {
        openGuard();
        ContentValues args = new ContentValues();
        args.put(COLUMN_PIC_SEL, id);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + rowId, null) > 0;
    }

    public int getCountPass() throws SQLException {
        openGuard();
        String countQuery = "SELECT " + COLUMN_TITLE + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public Cursor fetchPass(long rowId) throws SQLException {
        openGuard();
        Cursor mCursor = db.query(TABLE_NAME, null, COLUMN_ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void openGuard() throws SQLiteException {
        if (isOpen()) return;
        open();
        if (isOpen()) return;
        throw new SQLiteException("Could not open database");
    }
}
