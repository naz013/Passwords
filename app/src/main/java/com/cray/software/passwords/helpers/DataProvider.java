package com.cray.software.passwords.helpers;

import android.content.Context;
import android.database.Cursor;

import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.notes.NoteInterfaceImpl;
import com.cray.software.passwords.notes.NoteItem;
import com.cray.software.passwords.passwords.Password;
import com.cray.software.passwords.passwords.PasswordInterfaceImpl;
import com.cray.software.passwords.utils.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2016 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class DataProvider {
    public static final String TAG = "LOG_TAG";

    public static List<ListInterface> getData(Context context, boolean incPasswords, boolean incNotes) {
        List<ListInterface> list = new ArrayList<>();
        DataBase db = new DataBase(context);
        db.open();
        if (incPasswords) {
            Cursor c = db.fetchAllPasswords();
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new PasswordInterfaceImpl(getFromCursor(c)));
                } while (c.moveToNext());
                c.close();
            }
        }
        if (incNotes) {
            Cursor c = db.getNotes();
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new NoteInterfaceImpl(getNoteFromCursor(c)));
                } while (c.moveToNext());
                c.close();
            }
        }
        db.close();
        return sort(context, list);
    }

    private static List<ListInterface> sort(Context context, List<ListInterface> list) {
        String orderPrefs = Prefs.getInstance(context).getOrderBy();
        if (orderPrefs.matches(Constants.ORDER_DATE_A_Z)) {
            Collections.sort(list, (listInterface, t1) -> listInterface.getDate().compareTo(t1.getDate()));
        } else if (orderPrefs.matches(Constants.ORDER_DATE_Z_A)) {
            Collections.sort(list, (listInterface, t1) -> t1.getDate().compareTo(listInterface.getDate()));
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_A_Z)) {
            Collections.sort(list, (listInterface, t1) -> listInterface.getTitle().compareTo(t1.getTitle()));
        } else if (orderPrefs.matches(Constants.ORDER_TITLE_Z_A)) {
            Collections.sort(list, (listInterface, t1) -> t1.getTitle().compareTo(listInterface.getTitle()));
        }
        return list;
    }

    public static List<Password> getOriginalData(Context context) {
        List<Password> list = new ArrayList<>();
        DataBase db = new DataBase(context);
        db.open();
        Cursor c = db.fetchAllPasswords();
        if (c != null && c.moveToFirst()) {
            do {
                list.add(getFromCursor(c));
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return list;
    }

    public static List<NoteItem> getOriginalNotes(Context context) {
        List<NoteItem> list = new ArrayList<>();
        DataBase db = new DataBase(context);
        db.open();
        Cursor c = db.getNotes();
        if (c != null && c.moveToFirst()) {
            do {
                list.add(getNoteFromCursor(c));
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return list;
    }

    public static void savePassword(Context context, Password password) {
        DataBase db  = new DataBase(context);
        db.open();
        if (password.getId() != 0) {
            db.updatePass(password);
        } else {
            db.insertPass(password);
        }
        db.close();
    }

    public static void saveNote(Context context, NoteItem item) {
        DataBase db  = new DataBase(context);
        db.open();
        db.saveNote(item);
        db.close();
    }

    public static Password getPassword(Context context, long id) {
        DataBase db = new DataBase(context);
        db.open();
        Cursor c = db.fetchPass(id);
        Password password = null;
        if (c != null && c.moveToFirst()) {
            password = getFromCursor(c);
            c.close();
        }
        db.close();
        return password;
    }

    public static NoteItem getNote(Context context, long id) {
        DataBase db = new DataBase(context);
        db.open();
        Cursor c = db.getNote(id);
        NoteItem item = null;
        if (c != null && c.moveToFirst()) {
            item = getNoteFromCursor(c);
            c.close();
        }
        db.close();
        return item;
    }

    public static void deletePassword(Context context, Password password) {
        DataBase db = new DataBase(context);
        db.open();
        db.deletePass(password.getId());
        db.close();
    }

    public static void deleteNote(Context context, NoteItem noteItem) {
        DataBase db = new DataBase(context);
        db.open();
        db.deleteNote(noteItem.getId());
        db.close();
    }

    public static String getStarred(String login) {
        int length = login.length();
        StringBuilder sb = new StringBuilder();
        if (length > 3) {
            String sub = login.substring(0, 3);
            sb.append(sub);
            for (int i = 3; i < length; i++) {
                sb.append("*");
            }
            login = sb.toString();
        }
        return login;
    }

    private static Password getFromCursor(Cursor c) {
        String title = c.getString(c.getColumnIndex(DataBase.COLUMN_TITLE));
        String login = c.getString(c.getColumnIndex(DataBase.COLUMN_LOGIN));
        String password = c.getString(c.getColumnIndex(DataBase.COLUMN_PASSWORD));
        String url = c.getString(c.getColumnIndex(DataBase.COLUMN_URL));
        String comment = c.getString(c.getColumnIndex(DataBase.COLUMN_COMMENT));
        String date = c.getString(c.getColumnIndex(DataBase.COLUMN_DATE));
        String uuId = c.getString(c.getColumnIndex(DataBase.COLUMN_PIC_SEL));
        int color = c.getInt(c.getColumnIndex(DataBase.COLUMN_TECHNICAL));
        long id = c.getLong(c.getColumnIndex(DataBase.COLUMN_ID));
        return new Password(title, date, login, comment, url, id, color, password, uuId);
    }

    private static NoteItem getNoteFromCursor(Cursor c) {
        String title = c.getString(c.getColumnIndex(DataBase.COLUMN_SUMMARY));
        byte[] image = c.getBlob(c.getColumnIndex(DataBase.COLUMN_IMAGE));
        String date = c.getString(c.getColumnIndex(DataBase.COLUMN_DT));
        String uuId = c.getString(c.getColumnIndex(DataBase.COLUMN_UUID));
        int color = c.getInt(c.getColumnIndex(DataBase.COLUMN_COLOR));
        long id = c.getLong(c.getColumnIndex(DataBase.COLUMN_ID));
        return new NoteItem(id, title, uuId, date, color, image);
    }
}
