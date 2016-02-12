package com.cray.software.passwords.helpers;

import android.content.Context;
import android.database.Cursor;

import com.cray.software.passwords.R;
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
                int colorCircle;
                String colorDB = c.getString(c.getColumnIndex(Constants.COLUMN_TECHNICAL));
                if (colorDB == null) colorCircle = context.getResources().getColor(R.color.colorSemiTrGrayDark);
                else colorCircle = Integer.parseInt(colorDB);
                String title = c.getString(c.getColumnIndex(Constants.COLUMN_TITLE));
                title = Crypter.decrypt(title);
                String date = c.getString(c.getColumnIndex(Constants.COLUMN_DATE));
                date = Crypter.decrypt(date);
                long id = c.getLong(c.getColumnIndex(Constants.COLUMN_ID));
                list.add(new Password(title, date, id, colorCircle));
            } while (c.moveToNext());
        }
        if (c != null) c.close();
        db.close();
        return list;
    }
}
