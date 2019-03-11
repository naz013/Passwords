package com.cray.software.passwords.modern_ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.cray.software.passwords.data.DataBase
import com.cray.software.passwords.helpers.ListInterface
import com.cray.software.passwords.interfaces.Constants
import com.cray.software.passwords.data.NoteInterfaceImpl
import com.cray.software.passwords.data.PasswordInterfaceImpl
import com.cray.software.passwords.utils.Prefs
import com.cray.software.passwords.utils.launchDefault
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val db: DataBase by inject()
    private val prefs: Prefs by inject()
    private val homeLiveData = HomeLiveData()

    private inner class HomeLiveData internal constructor() : LiveData<List<ListInterface>>() {

        fun update() {
            launchDefault {
                val list = mutableListOf<ListInterface>()
                db.getPasswords().forEach { list.add(PasswordInterfaceImpl(it)) }
                db.getNotes().forEach { list.add(NoteInterfaceImpl(it)) }
                postValue(sort(list))
            }
        }
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
}
