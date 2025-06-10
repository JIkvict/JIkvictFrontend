package org.jikvict.browser.di

import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.jikvict.browser.viewmodel.NotFoundScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::NotFoundScreenViewModel)
    singleOf(::MakeJarScreenViewModel)
}
