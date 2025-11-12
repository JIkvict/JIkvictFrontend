package org.jikvict.browser.screens

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.EditAssignmentComponent
import org.jikvict.browser.viewmodel.EditAssignmentScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditAssignmentScreenComposable(
    defaultScreenScope: DefaultScreenScope,
    assignmentId: Long,
) {
    val vm = koinViewModel<EditAssignmentScreenViewModel>(key = assignmentId.toString())
    val assignment by vm.assignment.collectAsState()
    LaunchedEffect(assignmentId) {
        vm.clearAssignment()
        vm.loadAssignment(assignmentId)
        vm.loadGroups()
        vm.loadTasks()
    }
    val groups by vm.assignmentGroups.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val navHostController = LocalNavController.current
    assignment?.let { dto ->
        EditAssignmentComponent(
            defaultScreenScope,
            onNavigateBack = {
                navHostController.popBackStack()
            },
            assignment = dto,
            availableAssignmentGroups = groups,
            availableTasks = tasks,
            onUpdate = {
                vm.update(it)
            },
        )
    }

}

@SerialName("edit-assignment")
@Serializable
data class EditAssignmentScreen(
    private val assignmentId: Long
) : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = {
            EditAssignmentScreenComposable(
                it,
                assignmentId = assignmentId
            )
        }
    override val requiredRoles: List<String>
        get() = listOf("TEACHER")

}

object EditAssignmentScreenRouterRegistrar : ScreenRouterRegistrar<EditAssignmentScreen> {
    override val screen: KClass<EditAssignmentScreen>
        get() = EditAssignmentScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return params["assignmentId"]?.toLong()?.let { EditAssignmentScreen(it) } ?: NotFoundScreen()
    }
}

object EditAssignmentScreenRegistrar : ScreenRegistrar<EditAssignmentScreen> by createRegistrar()