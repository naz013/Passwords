package com.cray.software.passwords.helpers;

import android.content.Context;
import android.database.Cursor;

import com.cray.software.passwords.interfaces.Constants;

import java.util.ArrayList;

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

    public static ArrayList<Password> getData(Context context) {
        ArrayList<Password> list = new ArrayList<>();
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

    public static String getStarred(String login) {
        int length = login.length();
        StringBuilder sb = new StringBuilder();
        if (length > 5) {
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
        String title = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
        String login = c.getString(c.getColumnIndex(Constants.COLUMN_LOGIN));
        String password = c.getString(c.getColumnIndex(Constants.COLUMN_PASSWORD));
        String url = c.getString(c.getColumnIndex(Constants.COLUMN_URL));
        String comment = c.getString(c.getColumnIndex(Constants.COLUMN_COMMENT));
        String date = c.getString(c.getColumnIndex(Constants.COLUMN_DATE));
        String uuId = c.getString(c.getColumnIndex(Constants.COLUMN_PIC_SEL));
        int color = c.getInt(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
        long id = c.getLong(c.getColumnIndex(Constants.COLUMN_ID));
        return new Password(title, date, login, comment, url, id, color, password, uuId);
    }
}
