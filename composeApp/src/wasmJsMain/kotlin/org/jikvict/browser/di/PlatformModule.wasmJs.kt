package org.jikvict.browser.di

import io.ktor.client.engine.js.Js
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.api.apis.LongLivingTokenControllerApi
import org.jikvict.api.apis.PlagiarismControllerApi
import org.jikvict.api.apis.QueueStatusControllerApi
import org.jikvict.api.apis.SolutionCheckerControllerApi
import org.jikvict.api.apis.TaskStatusControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.api.apis.UsersControllerApi
import org.jikvict.browser.util.StateSaver
import org.jikvict.browser.util.simpleClientConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::StateSaver)

    single {
        AuthControllerApi(
            baseUrl = BACKEND_URL,
            httpClientConfig = ::simpleClientConfig,
            httpClientEngine = Js.create()
        )
    }
    single {
        LongLivingTokenControllerApi(
            baseUrl = BACKEND_URL,
            httpClientConfig = get(),
            httpClientEngine = Js.create()
        )
    }
    single { AssignmentControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single {
        SolutionCheckerControllerApi(
            baseUrl = BACKEND_URL,
            httpClientConfig = get(),
            httpClientEngine = Js.create()
        )
    }
    single { TaskStatusControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single {
        TeacherStudentControllerApi(
            baseUrl = BACKEND_URL,
            httpClientConfig = get(),
            httpClientEngine = Js.create()
        )
    }
    single {
        AssignmentGroupControllerApi(
            baseUrl = BACKEND_URL,
            httpClientConfig = get(),
            httpClientEngine = Js.create()
        )
    }
    single { UsersControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { QueueStatusControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { PlagiarismControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get(), httpClientEngine = Js.create()) }
}
