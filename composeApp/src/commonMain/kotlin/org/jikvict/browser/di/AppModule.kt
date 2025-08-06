package org.jikvict.browser.di

import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.jikvict.browser.viewmodel.NotFoundScreenViewModel
import org.jikvict.browser.viewmodel.TasksScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        // API clients
        single { AssignmentControllerApi() }

        // ViewModels
        singleOf(::NotFoundScreenViewModel)
        singleOf(::MakeJarScreenViewModel)
        singleOf(::TasksScreenViewModel)
    }
