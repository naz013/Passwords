package com.cray.software.passwords.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class Password(
        @SerializedName("title")
        var title: String?,
        @SerializedName("date")
        var date: String?,
        @SerializedName("login")
        var login: String?,
        @SerializedName("comment")
        var comment: String? = "",
        @SerializedName("url")
        var url: String?,
        @SerializedName("id")
        var id: Long = 0L,
        @SerializedName("color")
        var color: Int = 0,
        @SerializedName("password")
        var password: String?,
        @SerializedName("uuId")
        var uuId: String = UUID.randomUUID().toString()
)
