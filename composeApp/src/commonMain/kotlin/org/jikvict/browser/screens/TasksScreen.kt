package org.jikvict.browser.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider
import kotlin.reflect.KClass

@Serializable
data class Assignment(
    val id: Int,
    val title: String,
    val description: String,
    val taskNumber: Int
)

sealed class AssignmentsUiState {
    data object Loading : AssignmentsUiState()
    data class Success(
        val assignments: List<Assignment>,
        val currentPage: Int = 0,
        val hasMorePages: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : AssignmentsUiState()

    data class Error(val message: String) : AssignmentsUiState()
}

private suspend fun fetchAssignments(page: Int = 0, pageSize: Int = 20): AssignmentsUiState {
    return try {
        val response = AssignmentControllerApi().getAll(page = page, size = pageSize)

        if (!response.success) {
            return AssignmentsUiState.Error("Server error: ${response.status}")
        }

        val body = response.body()
        val content = body.content
        val pageMetadata = body.page

        if (content == null) {
            return AssignmentsUiState.Success(emptyList())
        }

        if (content.isEmpty()) {
            return AssignmentsUiState.Success(emptyList())
        }

        val totalPages = pageMetadata?.totalPages?.toInt() ?: 0
        val currentPage = pageMetadata?.number?.toInt() ?: 0
        val hasMorePages = currentPage < totalPages - 1

        val assignments = content.mapIndexed { index, dto ->
            Assignment(
                id = (page * pageSize) + index,
                title = dto.title,
                description = dto.description ?: "No description",
                taskNumber = dto.taskNumber
            )
        }

        AssignmentsUiState.Success(
            assignments = assignments,
            currentPage = currentPage,
            hasMorePages = hasMorePages,
            isLoadingMore = false
        )
    } catch (e: ClientRequestException) {
        AssignmentsUiState.Error("Request error: ${e.message}")
    } catch (e: ServerResponseException) {
        AssignmentsUiState.Error("Server error: ${e.message}")
    } catch (e: IOException) {
        AssignmentsUiState.Error("Network error: ${e.message}")
    } catch (e: Exception) {
        AssignmentsUiState.Error("Unknown error: ${e.message}")
    }
}

private suspend fun loadMoreAssignments(currentState: AssignmentsUiState.Success): AssignmentsUiState {
    if (!currentState.hasMorePages || currentState.isLoadingMore) {
        return currentState
    }

    val nextPage = currentState.currentPage + 1

    return try {
        val response = AssignmentControllerApi().getAll(page = nextPage, size = 20)

        if (!response.success) {
            return AssignmentsUiState.Error("Server error: ${response.status}")
        }

        val body = response.body()
        val content = body.content
        val pageMetadata = body.page

        if (content == null || content.isEmpty()) {
            return currentState.copy(hasMorePages = false, isLoadingMore = false)
        }

        val totalPages = pageMetadata?.totalPages?.toInt() ?: 0
        val currentPageFromResponse = pageMetadata?.number?.toInt() ?: 0
        val hasMorePages = currentPageFromResponse < totalPages - 1

        val newAssignments = content.mapIndexed { index, dto ->
            Assignment(
                id = (nextPage * 20) + index,
                title = dto.title,
                description = dto.description ?: "No description",
                taskNumber = dto.taskNumber
            )
        }

        val combinedAssignments = currentState.assignments + newAssignments

        AssignmentsUiState.Success(
            assignments = combinedAssignments,
            currentPage = nextPage,
            hasMorePages = hasMorePages,
            isLoadingMore = false
        )
    } catch (_: Exception) {
        currentState.copy(isLoadingMore = false)
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TasksScreenComposable(defaultScope: DefaultScreenScope): Unit = with(defaultScope) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()

    // Observe theme changes
    val themeSwitcher = ThemeSwitcherProvider.current
    val theme by themeSwitcher.isDark

    var uiState by remember { mutableStateOf<AssignmentsUiState>(AssignmentsUiState.Loading) }
    var selectedAssignmentId by remember { mutableIntStateOf(-1) }

    fun refreshAssignments() {
        uiState = AssignmentsUiState.Loading
        scope.launch {
            uiState = fetchAssignments(page = 0)
        }
    }

    fun loadMoreAssignments() {
        if (uiState is AssignmentsUiState.Success) {
            val successState = uiState as AssignmentsUiState.Success

            if (successState.hasMorePages && !successState.isLoadingMore) {
                uiState = successState.copy(isLoadingMore = true)

                scope.launch {
                    uiState = loadMoreAssignments(successState)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        uiState = fetchAssignments(page = 0)
    }

    val assignments = when (uiState) {
        is AssignmentsUiState.Success -> (uiState as AssignmentsUiState.Success).assignments
        else -> emptyList()
    }

    val isLoadingMore = when (uiState) {
        is AssignmentsUiState.Success -> (uiState as AssignmentsUiState.Success).isLoadingMore
        else -> false
    }

    val selectedAssignment: Assignment? = remember(selectedAssignmentId, assignments) {
        if (selectedAssignmentId != -1) {
            assignments.find { it.id == selectedAssignmentId }
        } else {
            null
        }
    }

    // Use theme state to force recomposition when theme changes
    key(theme) {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Place the RefreshButton at the top of the Column
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier.padding(4.dp),
                        ) {
                            RefreshButton(
                                isRefreshing = uiState is AssignmentsUiState.Loading,
                                onClick = { refreshAssignments() }
                            )
                        }
                    }

                    // Display the appropriate content based on the UI state
                    when (uiState) {
                        is AssignmentsUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularWavyProgressIndicator()
                            }
                        }

                        is AssignmentsUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "An error occurred",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = (uiState as AssignmentsUiState.Error).message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        is AssignmentsUiState.Success -> {
                            if (assignments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "No available assignments",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
                                    AssignmentListPane(
                                        assignments = assignments,
                                        onAssignmentClick = { assignment ->
                                            selectedAssignmentId = assignment.id
                                            scope.launch {
                                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, assignment.id)
                                            }
                                        },
                                        isLoadingMore = isLoadingMore,
                                        hasMorePages = (uiState as? AssignmentsUiState.Success)?.hasMorePages ?: false,
                                        onLoadMore = { loadMoreAssignments() }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            detailPane = {
                selectedAssignment?.let { assignment ->
                    AssignmentDetailPane(
                        assignment = assignment
                    )
                } ?: EmptyDetailPane()
            },
            modifier = Modifier.fitContentToScreen(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AssignmentListPane(
    assignments: List<Assignment>,
    onAssignmentClick: (Assignment) -> Unit,
    isLoadingMore: Boolean = false,
    hasMorePages: Boolean = false,
    onLoadMore: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(assignments) { assignment ->
                AssignmentListItem(
                    assignment = assignment,
                    onClick = { onAssignmentClick(assignment) },
                )

                if (assignment.id == assignments.lastOrNull()?.id && hasMorePages && !isLoadingMore) {
                    LaunchedEffect(assignment.id) {
                        onLoadMore()
                    }
                }
            }

            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularWavyProgressIndicator()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AssignmentListItem(
    assignment: Assignment,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = assignment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )

                Text(
                    text = "Task #${assignment.taskNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
context(scope: DefaultScreenScope)
private fun AssignmentDetailPane(
    assignment: Assignment
) {
    val scrollState = rememberScrollState()
    with(scope) {
        Column(
            modifier = Modifier
                .fitContentToScreen()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Task Details",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = assignment.title,
                onValueChange = { /* Read-only */ },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            OutlinedContentContainer(label = "Description") {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = scope.screenHeight * 0.5f),
                ) {
                    item {
                        Markdown(assignment.description)
                    }
                }

            }


            Text(
                text = "Task #${assignment.taskNumber}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "ID: ${assignment.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyDetailPane() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Select a task to view details",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Serializable
@SerialName("tasks")
class TasksScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { TasksScreenComposable(it) }
}

object TasksScreenRouterRegistrar : ScreenRouterRegistrar<TasksScreen> {
    override val screen: KClass<TasksScreen>
        get() = TasksScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return TasksScreen()
    }
}

object TasksScreenRegistrar : ScreenRegistrar<TasksScreen> by createRegistrar()

@Preview
@Composable
private fun TasksScreenPreview() {
    DefaultPreview {
        TasksScreenComposable(it)
    }
}

@Composable
fun OutlinedContentContainer(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            content()
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            modifier = Modifier
                .offset(x = 12.dp)
                .background(backgroundColor)
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {
    var totalRotation by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) totalRotation + 360f else totalRotation,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearEasing
        ),
        finishedListener = { finalValue ->
            if (isAnimating) {
                totalRotation = finalValue
                isAnimating = false
            }
        },
        label = "rotation"
    )

    LaunchedEffect(isRefreshing) {
        if (isRefreshing && !isAnimating) {
            isAnimating = true
        }
    }

    val iconTint = if (isRefreshing || isAnimating) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f)
    }

    val iconSize = 28.dp

    Button(
        onClick = onClick,
        enabled = !isRefreshing,
        modifier = Modifier.padding(4.dp).background(Color.Transparent, CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh",
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    rotationZ = rotation
                },
            tint = iconTint
        )
    }
}