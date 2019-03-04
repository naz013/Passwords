package com.cray.software.passwords.utils

import org.koin.dsl.module.Module
import org.koin.dsl.module.module

fun utilModule() = module {

}

fun viewModels() = module {

}

fun components(): List<Module> {
    return listOf(utilModule(), viewModels())
}