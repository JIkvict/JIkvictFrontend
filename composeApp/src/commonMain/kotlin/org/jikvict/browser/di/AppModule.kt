package org.jikvict.browser.di

import io.ktor.client.HttpClient
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.api.apis.SolutionCheckerControllerApi
import org.jikvict.api.apis.TaskStatusControllerApi
import org.jikvict.browser.auth.SessionManager
import org.jikvict.browser.util.ClientConfigProvider
import org.jikvict.browser.util.simpleClientConfig
import org.jikvict.browser.viewmodel.LoginScreenViewModel
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.jikvict.browser.viewmodel.NotFoundScreenViewModel
import org.jikvict.browser.viewmodel.TasksScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        // API clients
        singleOf(::simpleClientConfig)

        single { AuthControllerApi(httpClientConfig = ::simpleClientConfig) }
        singleOf(::SessionManager)

        singleOf(::ClientConfigProvider)

        single {
            val provider = get<ClientConfigProvider>()
            provider.provide()
        }

        single {
            HttpClient {
                val config = get<ClientConfigProvider>().provide()
                config(this)
            }
        }


        single { AssignmentControllerApi(httpClientConfig = get()) }
        single { SolutionCheckerControllerApi(httpClientConfig = get()) }
        single { TaskStatusControllerApi(httpClientConfig = get()) }


        // ViewModels
        singleOf(::NotFoundScreenViewModel)
        singleOf(::MakeJarScreenViewModel)
        singleOf(::TasksScreenViewModel)
        singleOf(::LoginScreenViewModel)
    }
