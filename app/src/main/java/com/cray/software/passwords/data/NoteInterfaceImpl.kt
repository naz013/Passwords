package com.cray.software.passwords.data

import com.cray.software.passwords.helpers.TImeUtils
import com.cray.software.passwords.modern_ui.list.DataAdapter
import com.cray.software.passwords.utils.SuperUtil

class NoteInterfaceImpl(private val mNote: NoteItem) : NoteListInterface {

    override val color: Int
        get() = mNote.color

    override val id: Long
        get() = mNote.id

    override val viewType: Int
        get() = DataAdapter.TYPE_NOTE

    override val title: String
        get() = SuperUtil.decrypt(mNote.summary)

    override val date: String
        get() = TImeUtils.getDateFromGmt(SuperUtil.decrypt(mNote.date))

    override val image: ByteArray
        get() = mNote.image
}
