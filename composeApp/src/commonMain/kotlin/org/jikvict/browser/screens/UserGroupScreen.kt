package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.UserGroupComponent
import org.jikvict.browser.viewmodel.UserGroupScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@Composable
fun UserGroupScreen(scope: DefaultScreenScope) = with(scope) {
    val viewModel = koinViewModel<UserGroupScreenViewModel>()
    val navHostController = LocalNavController.current
    LaunchedEffect(viewModel) {
        viewModel.loadGroups()
    }
    val assignmentGroupsDto by viewModel.groups.collectAsState()
    UserGroupComponent(
        assignmentGroups = assignmentGroupsDto,
        onNavigateBack = {
            with(navHostController) {
                AdminScreen.navigateTo()
            }
        },
        onGroupClick = {},
        onAddGroupClick = {
            with(navHostController) {
                CreateUserGroupScreen.navigateTo()
            }
        },
        scope = scope
    )

}
@Serializable
@SerialName("user-group")
object UserGroupScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { UserGroupScreen(it) }
}

object UserGroupScreenRegistrar : ScreenRegistrar<UserGroupScreen> by createRegistrar()

object UserGroupScreenRouterRegistrar : ScreenRouterRegistrar<UserGroupScreen> {
    override val screen: KClass<UserGroupScreen>
        get() = UserGroupScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return UserGroupScreen
    }
}