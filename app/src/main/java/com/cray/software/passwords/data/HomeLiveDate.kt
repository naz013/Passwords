package com.cray.software.passwords.data

import androidx.lifecycle.LiveData
import com.cray.software.passwords.helpers.ListInterface
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.launchDefault
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class HomeLiveDate(val addPasswords: Boolean = true, val addNotes: Boolean = true) : LiveData<List<ListInterface>>(), KoinComponent, DbObserver {

    private val db: DataBase by inject()
    private val prefs: Prefs by inject()

    init {
        db.addObserver(this)
        onChanged()
    }

    private fun sort(list: List<ListInterface>): List<ListInterface> {
        val orderPrefs = prefs.orderBy
        when (orderPrefs) {
            Constants.ORDER_DATE_A_Z -> list.sortedBy { it.date }
            Constants.ORDER_DATE_Z_A -> list.sortedByDescending { it.date }
            Constants.ORDER_TITLE_A_Z -> list.sortedBy { it.title }
            Constants.ORDER_TITLE_Z_A -> list.sortedByDescending { it.title }
        }
        return list
    }

    override fun onChanged() {
        launchDefault {
            val list = mutableListOf<ListInterface>()
            if (addPasswords) {
                db.getPasswords().forEach { list.add(PasswordInterfaceImpl(it)) }
            }
            if (addNotes) {
                db.getNotes().forEach { list.add(NoteInterfaceImpl(it)) }
            }
            postValue(sort(list))
        }
    }
}