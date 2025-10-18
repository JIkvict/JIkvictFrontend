package org.jikvict.browser.di

import io.ktor.client.engine.js.Js
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.api.apis.SolutionCheckerControllerApi
import org.jikvict.api.apis.TaskStatusControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.browser.util.StateSaver
import org.jikvict.browser.util.simpleClientConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::StateSaver)

    val baseUrl = "http://147.175.151.161"
    single {
        AuthControllerApi(
            baseUrl = baseUrl,
            httpClientConfig = ::simpleClientConfig,
            httpClientEngine = Js.create()
        )
    }
    single { AssignmentControllerApi(baseUrl = baseUrl, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { SolutionCheckerControllerApi(baseUrl = baseUrl, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { TaskStatusControllerApi(baseUrl = baseUrl, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { TeacherStudentControllerApi(baseUrl = baseUrl, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { AssignmentGroupControllerApi(baseUrl = baseUrl, httpClientConfig = get(), httpClientEngine = Js.create()) }
}
