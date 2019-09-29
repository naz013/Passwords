package com.cray.software.passwords.utils

import com.cray.software.passwords.data.DataBase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun utilModule() = module {
    single { DataBase(androidContext()) }
    single { Prefs(androidContext()) }
    single { ThemeUtil(androidContext(), get()) }
    single { Dialogues(get()) }
}