package com.cray.software.passwords.modern_ui.home

import androidx.lifecycle.ViewModel
import com.cray.software.passwords.data.DataBase
import com.cray.software.passwords.data.HomeLiveDate
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val db: DataBase by inject()

    val homeLiveData = HomeLiveDate()

}
