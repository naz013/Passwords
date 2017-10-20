package com.cray.software.passwords.passwords;

import com.google.gson.annotations.SerializedName;

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

    @SerializedName("title")
    private String title;
    @SerializedName("date")
    private String date;
    @SerializedName("login")
    private String login;
    @SerializedName("comment")
    private String comment;
    @SerializedName("url")
    private String url;
    @SerializedName("password")
    private String password;
    @SerializedName("id")
    private long id;
    @SerializedName("color")
    private int color;
    @SerializedName("uuId")
    private String uuId;

    public Password(String title, String date, String login, String comment, String url, long id, int color,
                    String password, String uuId) {
        this.title = title;
        this.date = date;
        this.login = login;
        this.comment = comment;
        this.url = url;
        this.id = id;
        this.color = color;
        this.password = password;
        this.uuId = uuId;
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
