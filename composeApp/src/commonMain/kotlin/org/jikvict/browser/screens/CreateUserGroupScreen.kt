package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.CreateAssignmentGroupComponent
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.User
import org.jikvict.browser.model.OperationResult
import org.koin.compose.koinInject
import kotlin.reflect.KClass


@Composable
fun CreateUserGroupScreen(scope: DefaultScreenScope) = with(scope) {
    val navHostController = LocalNavController.current

    val groupApi = koinInject<AssignmentGroupControllerApi>()

    var userIds by remember { mutableStateOf(setOf<Long>()) }
    LaunchedEffect(Unit) {
        val groups = groupApi.getAllAssignmentGroups()
        userIds = groups.body().flatMap { it.userIds }.toSet()
    }

    CreateAssignmentGroupComponent(
        onNavigateBack = {
            with(navHostController) {
                UserGroupScreen.navigateTo()
            }
        },
        onCreate = {
            try {
                val res = groupApi.createAssignmentGroup(it)
                if (res.status in 200 until 300) {

                    OperationResult.Success(res.body())
                } else {
                    OperationResult.Error("Server error")
                }
            } catch (e: Exception) {
                OperationResult.Error("Network error")
            }

        },
        allUsers = userIds.map { userId ->
            User(userId, "User$userIds", "email")
        },
        scope = scope,
        onNavigateToCreated = {
            println("I created a group")
        }
    )
}

@Serializable
@SerialName("create-user-group")
object CreateUserGroupScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { CreateUserGroupScreen(it) }
}


object CreateUserGroupScreenRouterRegistrar : ScreenRouterRegistrar<CreateUserGroupScreen> {
    override val screen: KClass<CreateUserGroupScreen>
        get() = CreateUserGroupScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return CreateUserGroupScreen
    }

}

object CreateUserGroupScreenRegistrar : ScreenRegistrar<CreateUserGroupScreen> by createRegistrar()