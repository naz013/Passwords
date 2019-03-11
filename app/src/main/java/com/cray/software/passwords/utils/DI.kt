package com.cray.software.passwords.utils

import com.cray.software.passwords.data.DataBase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

fun utilModule() = module {
    single { DataBase(androidContext()) }
    single { Prefs(androidContext()) }
    single { ThemeUtil(androidContext(), get()) }
}

fun viewModels() = module {

}

fun components(): List<Module> {
    return listOf(utilModule(), viewModels())
}