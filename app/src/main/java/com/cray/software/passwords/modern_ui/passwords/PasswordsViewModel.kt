package com.cray.software.passwords.modern_ui.passwords

import androidx.lifecycle.ViewModel
import com.cray.software.passwords.data.DataBase
import com.cray.software.passwords.data.HomeLiveDate
import org.koin.core.KoinComponent
import org.koin.core.inject

class PasswordsViewModel : ViewModel(), KoinComponent {

    private val db: DataBase by inject()

    val liveData = HomeLiveDate(addNotes = false)


}
