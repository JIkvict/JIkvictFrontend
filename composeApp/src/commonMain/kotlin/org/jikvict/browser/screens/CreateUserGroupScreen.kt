package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.UsersControllerApi
import org.jikvict.api.models.ProblemDetail
import org.jikvict.api.models.UserDto
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.CreateAssignmentGroupComponent
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.NavigateBackButton
import org.jikvict.browser.components.User
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DragDropHandler
import org.jikvict.browser.util.responsive.responsive
import org.jikvict.browser.util.setupDragAndDropHandlers
import org.koin.compose.koinInject
import kotlin.reflect.KClass


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateUserGroupScreen(scope: DefaultScreenScope) = with(scope) {
    val navHostController = LocalNavController.current
    val groupApi = koinInject<AssignmentGroupControllerApi>()
    val usersApi = koinInject<UsersControllerApi>()

    var usersResult: OperationResult<List<UserDto>> by remember { mutableStateOf(OperationResult.Loading()) }
    LaunchedEffect(Unit) {
        runCatching {
            val users = usersApi.getAllUsers()
            usersResult = OperationResult.Success(users.body())
        }.onFailure {
            usersResult = OperationResult.Error("Failed to load users")
        }
    }

    val onNavigateBack = {
        with(navHostController) {
            UserGroupScreen.navigateTo()
        }
    }

    when (val result = usersResult) {
        is OperationResult.Error -> {
            Box(
                modifier = Modifier.fitContentToScreen(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        NavigateBackButton(
                            onNavigateBack = onNavigateBack,
                            title = "Assignment Groups"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.responsive(mainColumnModifier),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                result.message,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        is OperationResult.Success -> {
            var uploading by remember { mutableStateOf(false) }
            var uploadStatus: OperationResult<List<UserDto>>? by remember { mutableStateOf(null) }
            val corScope = rememberCoroutineScope()

            // Drag and drop handler setup
            var isDragOver by remember { mutableStateOf(false) }
            var dragHandler by remember { mutableStateOf<DragDropHandler?>(null) }

            DisposableEffect(Unit) {
                dragHandler =
                    setupDragAndDropHandlers(
                        onDragEnter = { isDragOver = true },
                        onDragLeave = { isDragOver = false },
                        onDragOver = { /* keep drag state */ },
                        onFileDrop = { files ->
                            isDragOver = false
                            if (files.isNotEmpty() && !uploading) {
                                uploading = true
                                uploadStatus = OperationResult.Loading()
                                corScope.launch {
                                    val file = files.first()
                                    val aisIds =
                                        file.bytes.contentToString().split("\n").mapNotNull { it.toLongOrNull() }
                                            .map { it.toString() }
                                    runCatching {
                                        val result = usersApi.registerUsers(aisIds)
                                        uploadStatus = if (result.status in 200 until 300) {
                                            OperationResult.Success(result.body())
                                        } else {
                                            OperationResult.Error("Server error")
                                        }
                                    }.onFailure {
                                        when (it) {
                                            is ClientRequestException -> {
                                                val problem = it.response.body<ProblemDetail>()
                                                uploadStatus = OperationResult.Error(problem.detail ?: "Unknown error")
                                            }
                                            is ServerResponseException -> {
                                                val problem = it.response.body<ProblemDetail>()
                                                uploadStatus =
                                                    OperationResult.Error(problem.detail ?: "Unknown error")
                                            }

                                            else -> {
                                                uploadStatus = OperationResult.Error("Unknown error")
                                                println("Exception occurred")
                                                println(it.message)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    )

                onDispose {
                    dragHandler?.cleanup()
                    dragHandler = null
                }
            }

            if (isDragOver) {
                Box(
                    modifier = Modifier.fitContentToScreen(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.Rounded.FileUpload,
                            contentDescription = "Drag and drop file here",
                            modifier = Modifier.fillMaxSize(0.5f),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

            } else {
                CreateAssignmentGroupComponent(
                    preSelectedUsers = (uploadStatus as? OperationResult.Success)?.result?.map {
                        User(
                            id = it.id,
                            name = it.userNameField,
                            email = it.email
                        )
                    } ?: emptyList(),
                    onNavigateBack = onNavigateBack,
                    onCreate = {
                        try {
                            val res = groupApi.createAssignmentGroup(it)
                            if (res.status in 200 until 300) {

                                OperationResult.Success(res.body())
                            } else {
                                OperationResult.Error("Server error")
                            }
                        } catch (_: Exception) {
                            OperationResult.Error("Network error")
                        }

                    },
                    allUsers = result.result.map {
                        User(
                            id = it.id,
                            name = it.userNameField,
                            email = it.email
                        )
                    },
                    scope = scope,
                    onNavigateToCreated = {
                        println("I created a group")
                    }
                )
            }
        }

        is OperationResult.Loading -> {
            Box(
                modifier = Modifier.fitContentToScreen(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        NavigateBackButton(
                            onNavigateBack = onNavigateBack,
                            title = "Assignment Groups"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.responsive(mainColumnModifier),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            CircularWavyProgressIndicator()
                        }
                    }
                }
            }
        }
    }

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