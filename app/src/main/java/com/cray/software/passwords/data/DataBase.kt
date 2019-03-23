package com.cray.software.passwords.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Looper

class DataBase(private val context: Context) {

    private val observers: MutableList<DbObserver> = mutableListOf()

    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null
        private set

    private val isOpen: Boolean
        get() = database?.isOpen == true

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

    fun addObserver(observer: DbObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    fun removeObserver(observer: DbObserver) {
        if (observers.contains(observer)) {
            observers.remove(observer)
        }
    }

    private fun notifyObservers() {
        for (o in observers) o.onChanged()
    }

    fun open(): DataBase {
        dbHelper = DBHelper(context)
        database = dbHelper?.writableDatabase
        System.gc()
        return this
    }

    fun close() {
        dbHelper?.close()
    }

    private fun checkMain() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("DB operations not allowed on main thread!")
        }
    }

    fun saveNote(item: NoteItem) {
        checkMain()
        try {
            openGuard()
            val cv = ContentValues()
            cv.put(COLUMN_SUMMARY, item.summary)
            cv.put(COLUMN_UUID, item.key)
            cv.put(COLUMN_IMAGE, item.image)
            cv.put(COLUMN_DT, item.date)
            cv.put(COLUMN_COLOR, item.color)
            if (item.id == 0L) {
                database?.insert(TABLE_NOTES_NAME, null, cv)
            } else {
                database?.update(TABLE_NOTES_NAME, cv, COLUMN_ID + "=" + item.id, null)
            }
            notifyObservers()
        } catch (e: Exception) {
        }
    }

    fun deleteNote(rowId: Long) {
        checkMain()
        return try {
            openGuard()
            database?.delete(TABLE_NOTES_NAME, "$COLUMN_ID=$rowId", null) ?: 0
            notifyObservers()
        } catch (e: Exception) {
        }
    }

    fun getNote(rowId: Long): NoteItem? {
        checkMain()
        var item: NoteItem? = null
        try {
            openGuard()
            val c = database?.query(TABLE_NOTES_NAME, null, "$COLUMN_ID=$rowId", null, null, null, null, null)
            if (c != null && c.moveToFirst()) {
                item = getNoteFromCursor(c)
                c.close()
            }
            close()
        } catch (e: Exception) {
        }
        return item
    }

    fun getNotes(): List<NoteItem> {
        checkMain()
        return try {
            openGuard()
            val list = mutableListOf<NoteItem>()
            val c =  database?.query(TABLE_NOTES_NAME, null, null, null, null, null, null)
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(getNoteFromCursor(c))
                } while (c.moveToNext())
                c.close()
            }
            close()
            list
        } catch (e: Exception) {
            listOf()
        }
    }

    fun savePassword(password: Password) {
        checkMain()
        try {
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
            if (password.id == 0L) {
                database?.insert(TABLE_NAME, null, cv) ?: 0L
            } else {
                database?.update(TABLE_NAME, cv, COLUMN_ID + "=" + password.id, null)
            }
            notifyObservers()
        } catch (e: Exception) {
        }
    }

    fun deletePass(rowId: Long) {
        checkMain()
        try {
            openGuard()
            database?.delete(TABLE_NAME, "$COLUMN_ID=$rowId", null)
            notifyObservers()
        } catch (e: Exception) {
        }
    }

    fun getPasswords(): List<Password> {
        checkMain()
        return try {
            openGuard()
            val list = mutableListOf<Password>()
            val c =  database?.query(TABLE_NAME, null, null, null, null, null, null)
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(getFromCursor(c))
                } while (c.moveToNext())
                c.close()
            }
            close()
            list
        } catch (e: Exception) {
            listOf()
        }
    }

    fun getPassword(rowId: Long): Password? {
        checkMain()
        return try {
            openGuard()
            val c = database?.query(TABLE_NAME, null, "$COLUMN_ID=$rowId", null, null, null, null, null)
            var password: Password? = null
            if (c != null && c.moveToFirst()) {
                password = getFromCursor(c)
                c.close()
            }
            close()
            password
        } catch (e: Exception) {
            null
        }
    }

    @Throws(SQLiteException::class)
    fun openGuard() {
        if (isOpen) return
        open()
        if (isOpen) return
        throw SQLiteException("Could not open database")
    }

    private fun getFromCursor(c: Cursor): Password {
        val title = c.getString(c.getColumnIndex(DataBase.COLUMN_TITLE))
        val login = c.getString(c.getColumnIndex(DataBase.COLUMN_LOGIN))
        val password = c.getString(c.getColumnIndex(DataBase.COLUMN_PASSWORD))
        val url = c.getString(c.getColumnIndex(DataBase.COLUMN_URL))
        val comment = c.getString(c.getColumnIndex(DataBase.COLUMN_COMMENT))
        val date = c.getString(c.getColumnIndex(DataBase.COLUMN_DATE))
        val uuId = c.getString(c.getColumnIndex(DataBase.COLUMN_PIC_SEL))
        val color = c.getInt(c.getColumnIndex(DataBase.COLUMN_TECHNICAL))
        val id = c.getLong(c.getColumnIndex(DataBase.COLUMN_ID))
        return Password(title, date, login, comment, url, id, color, password, uuId)
    }

    private fun getNoteFromCursor(c: Cursor): NoteItem {
        val title = c.getString(c.getColumnIndex(DataBase.COLUMN_SUMMARY))
        val image = c.getBlob(c.getColumnIndex(DataBase.COLUMN_IMAGE))
        val date = c.getString(c.getColumnIndex(DataBase.COLUMN_DT))
        val uuId = c.getString(c.getColumnIndex(DataBase.COLUMN_UUID))
        val color = c.getInt(c.getColumnIndex(DataBase.COLUMN_COLOR))
        val id = c.getLong(c.getColumnIndex(DataBase.COLUMN_ID))
        return NoteItem(title, uuId, date, color, image, id)
    }

    companion object {
        const val DB_NAME = "appdb"
        const val DB_VERSION = 2
        const val TABLE_NAME = "passtab"
        const val TABLE_NOTES_NAME = "notes_tab"
        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title_enter"
        const val COLUMN_LOGIN = "login_enter"
        const val COLUMN_PASSWORD = "password_enter"
        const val COLUMN_URL = "link_enter"
        const val COLUMN_COMMENT = "comment_enter"
        const val COLUMN_DATE = "date_enter"
        const val COLUMN_EDIT_TIMES = "edit_count"
        const val COLUMN_PIC_SEL = "selected_pic"
        const val COLUMN_TECHNICAL = "technical_col"

        const val COLUMN_SUMMARY = "summary"
        const val COLUMN_UUID = "uuid"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_DT = "date"
        const val COLUMN_COLOR = "color"

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
