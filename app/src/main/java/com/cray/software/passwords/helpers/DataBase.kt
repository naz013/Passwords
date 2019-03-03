package com.cray.software.passwords.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.notes.NoteItem
import com.cray.software.passwords.passwords.Password
import com.cray.software.passwords.utils.Prefs

class DataBase(c: Context) {

    private var dbHelper: DBHelper? = null
    var database: SQLiteDatabase? = null
        private set

    val isOpen: Boolean
        get() = database != null && database!!.isOpen

    val notes: Cursor
        @Throws(SQLException::class)
        get() {
            openGuard()
            return database!!.query(TABLE_NOTES_NAME, null, null, null, null, null, null)
        }

    inner class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE)
            sqLiteDatabase.execSQL(NOTES_CREATE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            when (oldVersion) {
                1 -> db.execSQL(NOTES_CREATE)
            }
        }
    }

    init {
        mContext = c
    }

    @Throws(SQLiteException::class)
    fun open(): DataBase {
        dbHelper = DBHelper(mContext)
        database = dbHelper!!.writableDatabase
        System.gc()
        return this
    }

    fun close() {
        if (dbHelper != null) dbHelper!!.close()
    }

    fun saveNote(item: NoteItem) {
        openGuard()
        val cv = ContentValues()
        cv.put(COLUMN_SUMMARY, item.summary)
        cv.put(COLUMN_UUID, item.key)
        cv.put(COLUMN_IMAGE, item.image)
        cv.put(COLUMN_DT, item.date)
        cv.put(COLUMN_COLOR, item.color)
        if (item.id == 0L) {
            database!!.insert(TABLE_NOTES_NAME, null, cv)
        } else {
            database!!.update(TABLE_NOTES_NAME, cv, COLUMN_ID + "=" + item.id, null)
        }
    }

    fun deleteNote(rowId: Long): Boolean {
        openGuard()
        return database!!.delete(TABLE_NOTES_NAME, "$COLUMN_ID=$rowId", null) > 0
    }

    @Throws(SQLException::class)
    fun getNote(rowId: Long): Cursor? {
        openGuard()
        val mCursor = database!!.query(TABLE_NOTES_NAME, null, "$COLUMN_ID=$rowId", null, null, null, null, null)
        mCursor?.moveToFirst()
        return mCursor
    }

    fun insertPass(password: Password): Long {
        openGuard()
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, password.title)
        cv.put(COLUMN_LOGIN, password.login)
        cv.put(COLUMN_PASSWORD, password.password)
        cv.put(COLUMN_URL, password.url)
        cv.put(COLUMN_COMMENT, password.comment)
        cv.put(COLUMN_DATE, password.date)
        cv.put(COLUMN_TECHNICAL, password.color)
        cv.put(COLUMN_PIC_SEL, password.uuId)
        return database!!.insert(TABLE_NAME, null, cv)
    }

    fun updatePass(password: Password): Boolean {
        openGuard()
        val args = ContentValues()
        args.put(COLUMN_TITLE, password.title)
        args.put(COLUMN_LOGIN, password.login)
        args.put(COLUMN_PASSWORD, password.password)
        args.put(COLUMN_URL, password.url)
        args.put(COLUMN_COMMENT, password.comment)
        args.put(COLUMN_DATE, password.date)
        args.put(COLUMN_TECHNICAL, password.color)
        return database!!.update(TABLE_NAME, args, COLUMN_ID + "=" + password.id, null) > 0
    }

    fun deletePass(rowId: Long): Boolean {
        openGuard()
        return database!!.delete(TABLE_NAME, "$COLUMN_ID=$rowId", null) > 0
    }

    @Throws(SQLException::class)
    fun fetchAllPasswords(): Cursor {
        openGuard()
        return database!!.query(TABLE_NAME, null, null, null, null, null, null)
    }

    @Throws(SQLException::class)
    fun fetchPass(rowId: Long): Cursor? {
        openGuard()
        val mCursor = database!!.query(TABLE_NAME, null, "$COLUMN_ID=$rowId", null, null, null, null, null)
        mCursor?.moveToFirst()
        return mCursor
    }

    @Throws(SQLiteException::class)
    fun openGuard() {
        if (isOpen) return
        open()
        if (isOpen) return
        throw SQLiteException("Could not open database")
    }

    companion object {
        val DB_NAME = "appdb"
        val DB_VERSION = 2
        val TABLE_NAME = "passtab"
        val TABLE_NOTES_NAME = "notes_tab"
        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title_enter"
        val COLUMN_LOGIN = "login_enter"
        val COLUMN_PASSWORD = "password_enter"
        val COLUMN_URL = "link_enter"
        val COLUMN_COMMENT = "comment_enter"
        val COLUMN_DATE = "date_enter"
        val COLUMN_EDIT_TIMES = "edit_count"
        val COLUMN_PIC_SEL = "selected_pic"
        val COLUMN_TECHNICAL = "technical_col"

        val COLUMN_SUMMARY = "summary"
        val COLUMN_UUID = "uuid"
        val COLUMN_IMAGE = "image"
        val COLUMN_DT = "date"
        val COLUMN_COLOR = "color"
        internal var mContext: Context

        private val DB_CREATE = "create table " + TABLE_NAME + "(" +
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
                ");"

        private val NOTES_CREATE = "create table " + TABLE_NOTES_NAME + "(" +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_SUMMARY + " text, " +
                COLUMN_UUID + " VARCHAR(255), " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_DT + " VARCHAR(255), " +
                COLUMN_TECHNICAL + " VARCHAR(255), " +
                COLUMN_URL + " VARCHAR(255), " +
                COLUMN_COLOR + " integer, " +
                COLUMN_PIC_SEL + " VARCHAR(255), " +
                COLUMN_EDIT_TIMES + " integer" +
                ");"
    }
}
