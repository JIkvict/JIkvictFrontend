package org.jikvict.browser.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.LocalThemeSwitcherProvider
import org.jikvict.browser.viewmodel.TasksScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

@Serializable
data class Assignment(
    val id: Int,
    val title: String,
    val description: String,
    val taskNumber: Int,
)

sealed class AssignmentsUiState {
    data object Loading : AssignmentsUiState()

    data class Success(
        val assignments: List<Assignment>,
        val currentPage: Int = 0,
        val hasMorePages: Boolean = false,
        val isLoadingMore: Boolean = false,
    ) : AssignmentsUiState()

    data class Error(
        val message: String,
    ) : AssignmentsUiState()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TasksScreenComposable(defaultScope: DefaultScreenScope): Unit =
    with(defaultScope) {
        val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
        val scope = rememberCoroutineScope()

        val viewModel = koinViewModel<TasksScreenViewModel>()

        val themeSwitcher = LocalThemeSwitcherProvider.current
        val theme by themeSwitcher.isDark

        val uiState by viewModel.assignmentsState.collectAsState()
        val assignments by viewModel.assignments.collectAsState()
        val isLoadingMore by viewModel.isLoadingMore.collectAsState()

        var selectedAssignmentId by remember { mutableIntStateOf(-1) }

        fun refreshAssignments() {
            viewModel.refreshAssignments()
        }

        fun loadMoreAssignments() {
            viewModel.loadMoreAssignments()
        }

        val selectedAssignment: Assignment? =
            remember(selectedAssignmentId, assignments) {
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
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Box(
                                modifier = Modifier.padding(4.dp),
                            ) {
                                RefreshButton(
                                    isRefreshing = uiState is AssignmentsUiState.Loading,
                                    onClick = { refreshAssignments() },
                                )
                            }
                        }

                        when (uiState) {
                            is AssignmentsUiState.Loading -> {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .weight(1f),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularWavyProgressIndicator()
                                }
                            }

                            is AssignmentsUiState.Error -> {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .weight(1f)
                                            .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            text = "An error occurred",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = (uiState as AssignmentsUiState.Error).message,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }

                            is AssignmentsUiState.Success -> {
                                if (assignments.isEmpty()) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .weight(1f),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            Text(
                                                text = "No available assignments",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .weight(1f),
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
                                            onLoadMore = { loadMoreAssignments() },
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
                            assignment = assignment,
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
    onLoadMore: () -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        contentAlignment = Alignment.Center,
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
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = assignment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                )

                Text(
                    text = "Task #${assignment.taskNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Composable
context(scope: DefaultScreenScope)
private fun AssignmentDetailPane(assignment: Assignment) {
    val scrollState = rememberScrollState()
    with(scope) {
        Column(
            modifier =
                Modifier
                    .fitContentToScreen()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Task Details",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            OutlinedContentContainer(
                label = "Title",
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            OutlinedContentContainer(label = "Description") {
                LazyColumn(
                    modifier =
                        Modifier
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
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "ID: ${assignment.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyDetailPane() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Select a task to view details",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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

    override fun constructScreen(params: Map<String, String?>): NavigableScreen = TasksScreen()
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                    .padding(16.dp),
        ) {
            content()
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            modifier =
                Modifier
                    .offset(x = 12.dp)
                    .background(backgroundColor)
                    .padding(horizontal = 4.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit,
) {
    var totalRotation by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) totalRotation + 360f else totalRotation,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
        finishedListener = { finalValue ->
            if (isAnimating) {
                @Suppress("AssignedValueIsNeverRead")
                totalRotation = finalValue
                isAnimating = false
            }
        },
        label = "rotation",
    )

    LaunchedEffect(isRefreshing) {
        if (isRefreshing && !isAnimating) {
            isAnimating = true
        }
    }

    val iconTint =
        if (isRefreshing || isAnimating) {
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
        contentPadding = PaddingValues(4.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh",
            modifier =
                Modifier
                    .size(iconSize)
                    .graphicsLayer {
                        rotationZ = rotation
                    },
            tint = iconTint,
        )
    }
}
