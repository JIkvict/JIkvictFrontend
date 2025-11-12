package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.InfoAssignmentGroupComponent
import org.jikvict.browser.components.User
import org.jikvict.browser.viewmodel.EditAssignmentGroupViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

@Composable
fun EditAssignmentGroupScreenComposable(
    scope: DefaultScreenScope,
    groupId: Long
) {
    val navHostController = LocalNavController.current
    val vm = koinViewModel<EditAssignmentGroupViewModel>(key = groupId.toString())
    LaunchedEffect(groupId) {
        vm.clearGroup()
        vm.loadGroup(groupId)
        vm.loadUsers()
    }
    val group by vm.group.collectAsState()
    val users by vm.users.collectAsState()
    val assignments by vm.assignments.collectAsState()
    group?.let { dto ->
        InfoAssignmentGroupComponent(
            scope = scope,
            onNavigateBack = {
                navHostController.navigateBackOr(UserGroupScreen)
            },
            group = dto,
            allUsers = users.map {
                User(
                    id = it.id,
                    name = it.userNameField,
                    email = it.email
                )
            },
            onUpdate = {
                vm.update(it)
            },
            onNavigateToUpdated = {
                with(navHostController) {
                    EditAssignmentGroupScreen(groupId).navigateTo()
                }
            },
            assignments = assignments,
            onAssignmentClick = {
                with(navHostController) {
                    EditAssignmentScreen(it.id).navigateTo()
                }
            }
        )
    }
}

@Serializable
@SerialName("edit-assignment-group")
data class EditAssignmentGroupScreen(val groupId: Long) : TeacherScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = {
            EditAssignmentGroupScreenComposable(it, groupId)
        }
}

object EditAssignmentGroupScreenRouterRegistrar : ScreenRouterRegistrar<EditAssignmentGroupScreen> {
    override val screen: KClass<EditAssignmentGroupScreen>
        get() = EditAssignmentGroupScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return EditAssignmentGroupScreen(params["groupId"]?.toLong() ?: 0)
    }
}

object EditAssignmentGroupScreenRegistrar : ScreenRegistrar<EditAssignmentGroupScreen> by createRegistrar()