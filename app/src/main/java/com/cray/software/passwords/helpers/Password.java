package com.cray.software.passwords.helpers;

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
public class Password {
    private String title, date, login;
    private long id;
    private int color;

    public Password(String title, String date, long id, int color, String login) {
        this.title = title;
        this.date = date;
        this.id = id;
        this.color = color;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public int getColor() {
        return color;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
}
