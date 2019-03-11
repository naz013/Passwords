package com.cray.software.passwords.data

import com.cray.software.passwords.helpers.TImeUtils
import com.cray.software.passwords.passwords.PasswordsRecyclerAdapter
import com.cray.software.passwords.utils.SuperUtil

/**
 * Copyright 2017 Nazar Suhovich
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

class NoteInterfaceImpl(private val mNote: NoteItem) : NoteListInterface {

    override val color: Int
        get() = mNote.color

    override val id: Long
        get() = mNote.id

    override val viewType: Int
        get() = PasswordsRecyclerAdapter.NOTE

    override val title: String
        get() = SuperUtil.decrypt(mNote.summary)

    override val date: String
        get() = TImeUtils.getDateFromGmt(SuperUtil.decrypt(mNote.date))

    override val image: ByteArray
        get() = mNote.image
}
