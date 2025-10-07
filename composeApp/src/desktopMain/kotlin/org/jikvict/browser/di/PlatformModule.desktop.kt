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

    single { AuthControllerApi(httpClientConfig = ::simpleClientConfig) }
    single { AssignmentControllerApi(httpClientConfig = get()) }
    single { SolutionCheckerControllerApi(httpClientConfig = get()) }
    single { TaskStatusControllerApi(httpClientConfig = get()) }
    single { TeacherStudentControllerApi(httpClientConfig = get()) }
    single { AssignmentGroupControllerApi(httpClientConfig = get()) }


}
