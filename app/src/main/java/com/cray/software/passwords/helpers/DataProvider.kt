package com.cray.software.passwords.helpers

import android.content.Context
import android.database.Cursor
import android.util.Log

import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.notes.NoteInterfaceImpl
import com.cray.software.passwords.notes.NoteItem
import com.cray.software.passwords.passwords.Password
import com.cray.software.passwords.passwords.PasswordInterfaceImpl
import com.cray.software.passwords.utils.Prefs

import java.util.ArrayList
import java.util.Collections

/**
 * Copyright 2016 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object DataProvider {
    val TAG = "LOG_TAG"

    fun getData(context: Context, incPasswords: Boolean, incNotes: Boolean): List<ListInterface> {
        val list = ArrayList<ListInterface>()
        val db = DataBase(context)
        db.open()
        if (incPasswords) {
            val c = db.fetchAllPasswords()
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(PasswordInterfaceImpl(getFromCursor(c)))
                } while (c.moveToNext())
                c.close()
            }
        }
        if (incNotes) {
            val c = db.notes
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(NoteInterfaceImpl(getNoteFromCursor(c)))
                } while (c.moveToNext())
                c.close()
            }
        }
        db.close()
        Log.d(TAG, "getData: " + list.size)
        return sort(context, list)
    }

    private fun sort(context: Context, list: List<ListInterface>): List<ListInterface> {
        val orderPrefs = Prefs.getInstance(context).orderBy
        if (orderPrefs!!.matches(Constants.ORDER_DATE_A_Z.toRegex())) {
            Collections.sort(list) { listInterface, t1 -> listInterface.date.compareTo(t1.date) }
        } else if (orderPrefs.matches(Constants.ORDER_DATE_Z_A.toRegex())) {
            Collections.sort(list) { listInterface, t1 -> t1.date.compareTo(listInterface.date) }
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_A_Z.toRegex())) {
            Collections.sort(list) { listInterface, t1 -> listInterface.title.compareTo(t1.title) }
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_Z_A.toRegex())) {
            Collections.sort(list) { listInterface, t1 -> t1.title.compareTo(listInterface.title) }
        }
        return list
    }

    fun getOriginalData(context: Context): List<Password> {
        val list = ArrayList<Password>()
        val db = DataBase(context)
        db.open()
        val c = db.fetchAllPasswords()
        if (c != null && c.moveToFirst()) {
            do {
                list.add(getFromCursor(c))
            } while (c.moveToNext())
            c.close()
        }
        db.close()
        return list
    }

    fun getOriginalNotes(context: Context): List<NoteItem> {
        val list = ArrayList<NoteItem>()
        val db = DataBase(context)
        db.open()
        val c = db.notes
        if (c != null && c.moveToFirst()) {
            do {
                list.add(getNoteFromCursor(c))
            } while (c.moveToNext())
            c.close()
        }
        db.close()
        return list
    }

    fun savePassword(context: Context, password: Password) {
        val db = DataBase(context)
        db.open()
        val pass = getPassword(context, password.id)
        if (pass == null || pass.uuId != password.uuId) {
            password.id = 0
        }
        if (password.id != 0L) {
            db.updatePass(password)
        } else {
            db.insertPass(password)
        }
        db.close()
    }

    fun saveNote(context: Context, item: NoteItem) {
        val db = DataBase(context)
        db.open()
        val noteItem = getNote(context, item.id)
        if (noteItem == null || item.key != noteItem.key) {
            item.id = 0
        }
        db.saveNote(item)
        db.close()
    }

    fun getPassword(context: Context, id: Long): Password? {
        val db = DataBase(context)
        db.open()
        val c = db.fetchPass(id)
        var password: Password? = null
        if (c != null && c.moveToFirst()) {
            password = getFromCursor(c)
            c.close()
        }
        db.close()
        return password
    }

    fun getNote(context: Context, id: Long): NoteItem? {
        val db = DataBase(context)
        db.open()
        val c = db.getNote(id)
        var item: NoteItem? = null
        if (c != null && c.moveToFirst()) {
            item = getNoteFromCursor(c)
            c.close()
        }
        db.close()
        return item
    }

    fun deletePassword(context: Context, password: Password) {
        val db = DataBase(context)
        db.open()
        db.deletePass(password.id)
        db.close()
    }

    fun deleteNote(context: Context, noteItem: NoteItem) {
        val db = DataBase(context)
        db.open()
        db.deleteNote(noteItem.id)
        db.close()
    }

    fun getStarred(login: String): String {
        var login = login
        val length = login.length
        val sb = StringBuilder()
        if (length > 3) {
            val sub = login.substring(0, 3)
            sb.append(sub)
            for (i in 3 until length) {
                sb.append("*")
            }
            login = sb.toString()
        }
        return login
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
        return NoteItem(id, title, uuId, date, color, image)
    }
}
