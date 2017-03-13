package com.cray.software.passwords.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright 2017 Nazar Suhovich
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

public class TImeUtils {

    private static final DateFormat timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private static final DateFormat viewStamp = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public static String getGmtStamp() {
        return timeStamp.format(new Date(System.currentTimeMillis()));
    }

    public static String getDateFromGmt(String gmt) {
        try {
            Date date = timeStamp.parse(gmt);
            return viewStamp.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
