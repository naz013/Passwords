package com.cray.software.passwords.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class NoteItem(
        @SerializedName("summary")
        var summary: String? = null,
        @SerializedName("key")
        var key: String = UUID.randomUUID().toString(),
        @SerializedName("date")
        var date: String? = null,
        @SerializedName("color")
        var color: Int = 0,
        @SerializedName("image")
        var image: ByteArray = ByteArray(0),
        @SerializedName("id")
        var id: Long = 0L
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteItem

        if (summary != other.summary) return false
        if (key != other.key) return false
        if (date != other.date) return false
        if (color != other.color) return false
        if (!image.contentEquals(other.image)) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = summary?.hashCode() ?: 0
        result = 31 * result + key.hashCode()
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + color
        result = 31 * result + image.contentHashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
