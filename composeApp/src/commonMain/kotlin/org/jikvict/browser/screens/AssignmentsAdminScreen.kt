package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.AssignmentsComponent
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.viewmodel.AssignmentsAdminScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@Composable
fun AssignmentsAdminScreenComposable(scope: DefaultScreenScope) {
    val vm = koinViewModel<AssignmentsAdminScreenViewModel>()

    val assignments by vm.assignments.collectAsState()
    val navHostController = LocalNavController.current
    LaunchedEffect(vm) {
        vm.loadAssignments()
    }

    AssignmentsComponent(
        assignments = assignments,
        onNavigateBack = {
            with(navHostController) {
                AdminScreen.navigateTo()
            }
        },
        onAssignmentClick = {
        },
        scope = scope,
        onAddAssignmentClick = {
            with(navHostController) {
                CreateAssignmentScreen.navigateTo()
            }
        }
    )
}

@Serializable
@SerialName("admin-assignments")
object AssignmentsAdminScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { AssignmentsAdminScreenComposable(it) }
    override val requiredRoles: List<String>
        get() = listOf("TEACHER")

}

object AssignmentsAdminScreenRouterRegistrar : ScreenRouterRegistrar<AssignmentsAdminScreen> {
    override val screen: KClass<AssignmentsAdminScreen>
        get() = AssignmentsAdminScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return AssignmentsAdminScreen
    }

}

object AssignmentsAdminScreenRegistrar : ScreenRegistrar<AssignmentsAdminScreen> by createRegistrar()