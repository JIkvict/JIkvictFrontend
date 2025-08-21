package org.jikvict.browser.di

import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.api.apis.SolutionCheckerControllerApi
import org.jikvict.api.apis.TaskStatusControllerApi
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
        singleOf(::clientConfig)

        single { AssignmentControllerApi(httpClientConfig = ::clientConfig) }
        single { AuthControllerApi(httpClientConfig = ::clientConfig) }
        single { SolutionCheckerControllerApi(httpClientConfig = ::clientConfig) }
        single { TaskStatusControllerApi(httpClientConfig = ::clientConfig) }

        // ViewModels
        singleOf(::NotFoundScreenViewModel)
        singleOf(::MakeJarScreenViewModel)
        singleOf(::TasksScreenViewModel)
        singleOf(::LoginScreenViewModel)
    }
