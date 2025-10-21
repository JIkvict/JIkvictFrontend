package org.jikvict.browser.di

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

    val baseUrl = BACKEND_URL

    single { AuthControllerApi(baseUrl= baseUrl,httpClientConfig = ::simpleClientConfig) }
    single { AssignmentControllerApi(baseUrl= baseUrl,httpClientConfig = get()) }
    single { SolutionCheckerControllerApi(baseUrl= baseUrl,httpClientConfig = get()) }
    single { TaskStatusControllerApi(baseUrl= baseUrl,httpClientConfig = get()) }
    single { TeacherStudentControllerApi(baseUrl= baseUrl,httpClientConfig = get()) }
    single { AssignmentGroupControllerApi(baseUrl= baseUrl,httpClientConfig = get()) }


}
