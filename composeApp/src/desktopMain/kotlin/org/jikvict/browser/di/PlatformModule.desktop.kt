package org.jikvict.browser.di

import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.AuthControllerApi
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


    single { AuthControllerApi(baseUrl = BACKEND_URL, httpClientConfig = ::simpleClientConfig) }
    single { AssignmentControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get()) }
    single { SolutionCheckerControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get()) }
    single { TaskStatusControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get()) }
    single { TeacherStudentControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get()) }
    single { AssignmentGroupControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get()) }
    single { UsersControllerApi(baseUrl = BACKEND_URL, httpClientConfig = get()) }


}
