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

    public long insertPass (String title, String login, String password, String url,
                            String comment, String date, String color, String uuID) {
        openGuard();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_LOGIN, login);
        cv.put(COLUMN_PASSWORD, password);
        cv.put(COLUMN_URL, url);
        cv.put(COLUMN_COMMENT, comment);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TECHNICAL, color);
        cv.put(COLUMN_PIC_SEL, uuID);
        //Log.d(LOG_TAG, "data is inserted " + cv);
        return db.insert(TABLE_NAME, null, cv);
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
        if (orderPrefs.matches(Constants.ORDER_DATE_A_Z)){
            order = COLUMN_DATE + " ASC";
        } else if (orderPrefs.matches(Constants.ORDER_DATE_Z_A)){
            order = COLUMN_DATE + " DESC";
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_A_Z)){
            order = COLUMN_TITLE + " ASC";
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_Z_A)){
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

    public Cursor fetchAllNames() throws SQLException {
        openGuard();
        return db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_TITLE, COLUMN_TECHNICAL}, null, null, null, null, null);
    }

    public Cursor fetchOnlyName(long rowId) throws SQLException {
        openGuard();
        Cursor mCursor = db.query(TABLE_NAME, new String[] {COLUMN_TITLE}, COLUMN_ID + "=" + rowId, null, null, null, null, null);
        return mCursor;
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
        Cursor mCursor = db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_TITLE, COLUMN_LOGIN,
                COLUMN_PASSWORD, COLUMN_URL, COLUMN_COMMENT, COLUMN_DATE, COLUMN_TECHNICAL, COLUMN_PIC_SEL},
                COLUMN_ID + "=" + rowId, null, null, null, null, null);
        //Log.d(LOG_TAG, "cursor received");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean updatePass(long rowId, String tt, String lg, String ps, String ul,
                              String cm, String dt, String ct) {
        openGuard();
        ContentValues args = new ContentValues();
        args.put(COLUMN_TITLE, tt);
        args.put(COLUMN_LOGIN, lg);
        args.put(COLUMN_PASSWORD, ps);
        args.put(COLUMN_URL, ul);
        args.put(COLUMN_COMMENT, cm);
        args.put(COLUMN_DATE, dt);
        args.put(COLUMN_TECHNICAL, ct);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + rowId, null) > 0;
    }

    public void openGuard() throws SQLiteException {
        if(isOpen()) return;
        open();
        if(isOpen()) return;
        //Log.d(LOG_TAG, "open guard failed");
        throw new SQLiteException("Could not open database");
    }
}
