package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.AssignmentInfoComponent
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.viewmodel.AssignmentInfoScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AssignmentInfoScreenComposable(
    scope: DefaultScreenScope,
    assignmentId: Long
) {

    val vm = koinViewModel<AssignmentInfoScreenViewModel>(key = assignmentId.toString())


    LaunchedEffect(assignmentId) {
        vm.loadAssignments(assignmentId)
        vm.loadGroup().join()
        vm.loadUsers()
    }
    val assignment by vm.assignment.collectAsState()
    val groups by vm.groups.collectAsState()
    val users by vm.users.collectAsState()

    if (assignment == null || groups == null || users == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularWavyProgressIndicator()
        }
        return
    }

    val navHostController = LocalNavController.current

    AssignmentInfoComponent(
        scope = scope,
        assignment = assignment!!,
        availableUsers = users!!.distinctBy { it.userNameField },
        availableGroups = groups!!,
        infoSupplier = { selectedUsers, selectedGroups ->
            vm.loadInfos(
                groupIds = selectedGroups.mapNotNull { it.id },
                userIds = selectedUsers.map { it.id }.distinct()
            )
        },
        onNavigateBack = {
            navHostController.navigateBackOr(AssignmentsAdminScreen)
        },
        onEditClick = {
            with(navHostController) {
                EditAssignmentScreen(it.id).navigateTo()
            }
        },
        onDownloadClick = {
            vm.downloadZipAndSave(it.id)
        }
    )
}

@Serializable
@SerialName("admin-assignment-info")
data class AssignmentInfoScreen(val assignmentId: Long) : TeacherScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { AssignmentInfoScreenComposable(it, assignmentId) }
}

object AssignmentInfoScreenRouterRegistrar : ScreenRouterRegistrar<AssignmentInfoScreen> {
    override val screen: KClass<AssignmentInfoScreen>
        get() = AssignmentInfoScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return params["assignmentId"]?.toLong()?.let { AssignmentInfoScreen(it) } ?: NotFoundScreen()
    }
}

object AssignmentInfoScreenRegistrar : ScreenRegistrar<AssignmentInfoScreen> by createRegistrar()
