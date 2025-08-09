package org.jikvict.browser.di

import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.browser.util.clientConfig
import org.jikvict.browser.viewmodel.LoginScreenViewModel
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.jikvict.browser.viewmodel.NotFoundScreenViewModel
import org.jikvict.browser.viewmodel.TasksScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        // API clients
        single { AssignmentControllerApi(httpClientConfig = ::clientConfig) }
        single { AuthControllerApi(httpClientConfig = ::clientConfig) }

        // ViewModels
        singleOf(::NotFoundScreenViewModel)
        singleOf(::MakeJarScreenViewModel)
        singleOf(::TasksScreenViewModel)
        singleOf(::LoginScreenViewModel)
    }
