package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.CreateAssignmentComponent
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.NavigateBackButton
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.responsive.responsive
import org.jikvict.browser.viewmodel.CreateAssignmentScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateAssignmentScreenComposable(scope: DefaultScreenScope) = with(scope) {
    val vm = koinViewModel<CreateAssignmentScreenViewModel>()
    val navHostController = LocalNavController.current

    val availableGroups by vm.groups.collectAsState()
    val availableTasks by vm.availableTasks.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadGroups()
        vm.loadTasks()
    }

    val onNavigateBack = {
        with(navHostController) {
            AssignmentsAdminScreen.navigateTo()
        }
    }

    var creating by remember { mutableStateOf(false) }
    var createError by remember { mutableStateOf<String?>(null) }
    val corScope = rememberCoroutineScope()

    when {
        isLoading -> {
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
                            title = "Assignments"
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

        createError != null -> {
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
                            onNavigateBack = {
                                createError = null
                                onNavigateBack()
                            },
                            title = "Assignments"
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
                                createError!!,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        creating -> {
            Box(
                modifier = Modifier.fitContentToScreen(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularWavyProgressIndicator()
                }
            }
        }

        else -> {
            CreateAssignmentComponent(
                scope = scope,
                onNavigateBack = onNavigateBack,
                availableAssignmentGroups = availableGroups,
                availableTasks = availableTasks,
                onCreate = { assignment ->
                    if (!creating) {
                        creating = true
                        createError = null
                        corScope.launch {
                            val result = vm.createAssignment(assignment)
                            creating = false
                            when (result) {
                                is OperationResult.Success -> {
                                    onNavigateBack()
                                }

                                is OperationResult.Error -> {
                                    createError = result.message
                                }

                                else -> {
                                    createError = "Unknown error occurred"
                                }
                            }
                        }
                        OperationResult.Loading()
                    } else {
                        OperationResult.Loading()
                    }
                }
            )
        }
    }
}


@Serializable
@SerialName("create-assignment")
object CreateAssignmentScreen : TeacherWriteScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = {
            CreateAssignmentScreenComposable(it)
        }
}

object CreateAssignmentScreenRouterRegistrar : ScreenRouterRegistrar<CreateAssignmentScreen> {
    override val screen: KClass<CreateAssignmentScreen>
        get() = CreateAssignmentScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return CreateAssignmentScreen
    }
}

object CreateAssignmentScreenRegistrar : ScreenRegistrar<CreateAssignmentScreen> by createRegistrar()