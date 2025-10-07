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


    single { AuthControllerApi(httpClientConfig = ::simpleClientConfig, httpClientEngine = Js.create()) }
    single { AssignmentControllerApi(httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { SolutionCheckerControllerApi(httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { TaskStatusControllerApi(httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { TeacherStudentControllerApi(httpClientConfig = get(), httpClientEngine = Js.create()) }
    single { AssignmentGroupControllerApi(httpClientConfig = get(), httpClientEngine = Js.create()) }
}
