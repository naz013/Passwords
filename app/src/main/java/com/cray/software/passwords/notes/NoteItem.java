package com.cray.software.passwords.notes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

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

public class NoteItem {
    @SerializedName("summary")
    private String summary;
    @SerializedName("key")
    private String key;
    @SerializedName("date")
    private String date;
    @SerializedName("color")
    private int color;
    @SerializedName("image")
    private byte[] image;
    @SerializedName("id")
    @Expose
    private long id;

    public NoteItem(long id, String summary, String key, String date, int color, byte[] image) {
        this.id = id;
        this.summary = summary;
        this.key = key;
        this.date = date;
        this.color = color;
        this.image = image;
    }

    public NoteItem() {
        setKey(UUID.randomUUID().toString());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
