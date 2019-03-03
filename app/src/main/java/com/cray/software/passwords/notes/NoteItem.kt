package com.cray.software.passwords.notes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Arrays
import java.util.UUID

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

class NoteItem {
    @SerializedName("summary")
    var summary: String? = null
    @SerializedName("key")
    var key: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("color")
    var color: Int = 0
    @SerializedName("image")
    var image: ByteArray? = null
    @SerializedName("id")
    @Expose
    var id: Long = 0

    constructor(id: Long, summary: String, key: String, date: String, color: Int, image: ByteArray) {
        this.id = id
        this.summary = summary
        this.key = key
        this.date = date
        this.color = color
        this.image = image
    }

    constructor() {
        key = UUID.randomUUID().toString()
    }

    override fun toString(): String {
        return "NoteItem{" +
                "summary='" + summary + '\''.toString() +
                ", key='" + key + '\''.toString() +
                ", date='" + date + '\''.toString() +
                ", color=" + color +
                ", image=" + Arrays.toString(image) +
                ", id=" + id +
                '}'.toString()
    }
}
