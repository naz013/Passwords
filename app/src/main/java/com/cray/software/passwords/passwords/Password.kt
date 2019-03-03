package com.cray.software.passwords.passwords

import com.google.gson.annotations.SerializedName

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

class Password(@field:SerializedName("title")
               var title: String?, @field:SerializedName("date")
               var date: String?, @field:SerializedName("login")
               var login: String?, @field:SerializedName("comment")
               var comment: String?, @field:SerializedName("url")
               var url: String?, @field:SerializedName("id")
               var id: Long, @field:SerializedName("color")
               var color: Int,
               @field:SerializedName("password")
               var password: String?, @field:SerializedName("uuId")
               var uuId: String?)
