package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.StudentsComponent
import org.jikvict.browser.viewmodel.StudentsScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

@Composable
fun StudentsScreenComposable(scope: DefaultScreenScope, userName: String? = null) {
    val vm = koinViewModel<StudentsScreenViewModel>()
    val navHostController = LocalNavController.current
    LaunchedEffect(Unit) { vm.loadUsers() }

    val users by vm.users.collectAsState()

    StudentsComponent(
        availableUsers = users,
        statsProvider = { user -> vm.getStudentStats(user) },
        onDownloadClick = { vm.downloadZipAndSave(it.id) },
        scope = scope,
        onGroupClick = {
            with(navHostController) { EditAssignmentGroupScreen(it.id!!).navigateTo() }
        },
        onNavigateBack = { navHostController.navigateBackOr(UserGroupScreen) },
        initialUserName = userName
    )
}

@Serializable
@SerialName("students-screen")
data class StudentsScreen(val userName: String? = null) : TeacherScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { StudentsScreenComposable(it, userName) }
}

object StudentsScreenRouterRegistrar : ScreenRouterRegistrar<StudentsScreen> {
    override val screen: KClass<StudentsScreen>
        get() = StudentsScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return StudentsScreen(params["userName"])
    }
}

object StudentsScreenRegistrar : ScreenRegistrar<StudentsScreen> by createRegistrar()
