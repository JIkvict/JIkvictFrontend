package org.jikvict.browser.di

import io.ktor.client.HttpClient
import org.jikvict.browser.auth.SessionManager
import org.jikvict.browser.util.ClientConfigProvider
import org.jikvict.browser.viewmodel.LoginScreenViewModel
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.jikvict.browser.viewmodel.NotFoundScreenViewModel
import org.jikvict.browser.viewmodel.TasksScreenViewModel
import org.jikvict.browser.viewmodel.AdminScreenViewModel
import org.jikvict.browser.viewmodel.AssignmentsAdminScreenViewModel
import org.jikvict.browser.viewmodel.CreateAssignmentScreenViewModel
import org.jikvict.browser.viewmodel.EditAssignmentScreenViewModel
import org.jikvict.browser.viewmodel.UserGroupScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        // API clients
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


        // ViewModels
        singleOf(::NotFoundScreenViewModel)
        singleOf(::MakeJarScreenViewModel)
        singleOf(::TasksScreenViewModel)
        singleOf(::LoginScreenViewModel)
        singleOf(::AdminScreenViewModel)
        singleOf(::UserGroupScreenViewModel)
        singleOf(::AssignmentsAdminScreenViewModel)
        singleOf(::CreateAssignmentScreenViewModel)
        singleOf(::EditAssignmentScreenViewModel)
    }
