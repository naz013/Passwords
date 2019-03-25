package com.cray.software.passwords.utils

import com.cray.software.passwords.data.DataBase
import com.cray.software.passwords.modern_ui.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

fun utilModule() = module {
    single { DataBase(androidContext()) }
    single { Prefs(androidContext()) }
    single { ThemeUtil(androidContext(), get()) }
    single { Dialogues(get()) }
}

fun viewModels() = module {
    viewModel { HomeViewModel() }
}

fun components(): List<Module> {
    return listOf(utilModule(), viewModels())
}