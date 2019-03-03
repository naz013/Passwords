package com.cray.software.passwords.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class PostDataBase(c: Context) {
    private var dbHelper: DBHelper? = null
    var database: SQLiteDatabase? = null
        private set

    val isOpen: Boolean
        get() = database != null && database!!.isOpen

    val countPass: Int
        @Throws(SQLException::class)
        get() {
            openGuard()
            val countQuery = "SELECT $COLUMN_FILE_NAME FROM $TABLE_NAME"
            val cursor = database!!.rawQuery(countQuery, null)
            val cnt = cursor.count
            cursor.close()
            return cnt
        }

    inner class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}


    }

    init {
        mContext = c
    }

    @Throws(SQLiteException::class)
    fun open(): PostDataBase {
        dbHelper = DBHelper(mContext)

        database = dbHelper!!.writableDatabase

        System.gc()
        return this
    }

    fun close() {
        if (dbHelper != null)
            dbHelper!!.close()
    }

    fun addProcess(file: String, path: String, code: Int): Long {
        openGuard()
        val cv = ContentValues()
        cv.put(COLUMN_FILE_NAME, file)
        cv.put(COLUMN_PATH, path)
        cv.put(COLUMN_PROCESS, code)
        //cv.put(COLUMN_TECH, ul);
        //cv.put(COLUMN_SECOND_TECH, cm);
        //Log.d(LOG_TAG, "data is inserted " + cv);
        return database!!.insert(TABLE_NAME, null, cv)
    }

    fun deleteProcess(rowId: Long): Boolean {
        openGuard()
        return database!!.delete(TABLE_NAME, "$COLUMN_ID=$rowId", null) > 0
    }

    @Throws(SQLException::class)
    fun fetchProcess(rowId: Long): Cursor {
        openGuard()
        return database!!.query(TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_FILE_NAME, COLUMN_PATH, COLUMN_PROCESS, COLUMN_TECH, COLUMN_SECOND_TECH), "$COLUMN_ID=$rowId", null, null, null, null, null)
    }

    @Throws(SQLException::class)
    fun fetchCodes(): Cursor {
        openGuard()
        return database!!.query(TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_PROCESS), null, null, null, null, null)
    }

    @Throws(SQLiteException::class)
    fun openGuard() {
        if (isOpen) return
        open()
        if (isOpen) return
        throw SQLiteException("Could not open database")
    }

    companion object {
        val DB_NAME = "post_process"
        val DB_VERSION = 1
        val TABLE_NAME = "dbx_process"
        val COLUMN_ID = "_id"
        val COLUMN_FILE_NAME = "file_name"
        val COLUMN_PROCESS = "process_code"
        val COLUMN_PATH = "file_path"
        val COLUMN_TECH = "first_tech"
        val COLUMN_SECOND_TECH = "second_tech"
        internal var mContext: Context

        private val DB_CREATE = "create table " + TABLE_NAME + "(" +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_FILE_NAME + " VARCHAR(255), " +
                COLUMN_PROCESS + " integer, " +
                COLUMN_PATH + " VARCHAR(255), " +
                COLUMN_TECH + " VARCHAR(255), " +
                COLUMN_SECOND_TECH + " VARCHAR(255)" +
                ");"
    }
}
