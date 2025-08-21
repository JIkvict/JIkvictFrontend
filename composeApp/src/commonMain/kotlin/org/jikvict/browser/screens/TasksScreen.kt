package org.jikvict.browser.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.rounded.FileUpload
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
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import dev.tclement.fonticons.FontIcon
import dev.tclement.fonticons.rememberStaticIconFont
import jikvictfrontend.composeapp.generated.resources.`MaterialSymbolsOutlined_VariableFont_FILL,GRAD,opsz,wght`
import jikvictfrontend.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.DragDropHandler
import org.jikvict.browser.util.LocalThemeSwitcherProvider
import org.jikvict.browser.util.setupDragAndDropHandlers
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

data class TaskNotification(
    val message: String,
    val type: NotificationType,
    val isVisible: Boolean = true
)

enum class NotificationType {
    SUCCESS, ERROR
}

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
        var notification by remember { mutableStateOf<TaskNotification?>(null) }


        fun refreshAssignments() {
            viewModel.refreshAssignments()
        }

        fun loadMoreAssignments() {
            viewModel.loadMoreAssignments()
        }

        fun showNotification(message: String, type: NotificationType) {
            notification = TaskNotification(message, type, true)
            // Auto-hide after 5 seconds
            scope.launch {
                delay(5000)
                notification = null
            }
        }

        fun hideNotification() {
            notification = null
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
                                    .fillMaxWidth(),
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
                                                    navigator.navigateTo(
                                                        ListDetailPaneScaffoldRole.Detail,
                                                        assignment.id
                                                    )
                                                }
                                            },
                                            isLoadingMore = isLoadingMore,
                                            hasMorePages = (uiState as? AssignmentsUiState.Success)?.hasMorePages
                                                ?: false,
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
                            navigator = navigator,
                            showNotification = ::showNotification
                        )
                    } ?: EmptyDetailPane()
                },
                modifier = Modifier.fitContentToScreen(),
            )
        }

        notification?.let { notif ->
            TaskNotificationOverlay(
                notification = notif,
                onDismiss = { hideNotification() }
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
context(scope: DefaultScreenScope)
private fun AssignmentDetailPane(
    assignment: Assignment,
    navigator: ThreePaneScaffoldNavigator<Int>,
    showNotification: (String, NotificationType) -> Unit = { _, _ -> }
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Task Details",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                val scope = rememberCoroutineScope()
                val iconFont = rememberStaticIconFont(
                    fontResource = Res.font.`MaterialSymbolsOutlined_VariableFont_FILL,GRAD,opsz,wght`
                )
                navigator.let { nav ->
                    if (nav.canNavigateBack()) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val isHovered by interactionSource.collectIsHoveredAsState()

                        val animatedTint by animateColorAsState(
                            targetValue = if (isHovered) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            label = "back_icon_tint"
                        )

                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                scope.launch {
                                    nav.navigateBack()
                                }
                            }
                        ) {
                            FontIcon(
                                iconFont = iconFont,
                                icon = '\uf6ff',
                                contentDescription = "Back",
                                tint = animatedTint,
                                modifier = Modifier.size(32.dp).graphicsLayer {
                                    rotationY = 180f
                                },
                            )
                        }


                    }
                }

            }

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

            // Action buttons: Download and Upload with drag and drop support
            val vm = koinViewModel<TasksScreenViewModel>()
            var downloading by remember { mutableStateOf(false) }
            var uploading by remember { mutableStateOf(false) }
            var uploadStatus by remember { mutableStateOf("") }
            val corScope = rememberCoroutineScope()

            // Drag and drop handler setup
            var isDragOver by remember { mutableStateOf(false) }
            var dragHandler by remember { mutableStateOf<DragDropHandler?>(null) }

            DisposableEffect(Unit) {
                dragHandler = setupDragAndDropHandlers(
                    onDragEnter = { isDragOver = true },
                    onDragLeave = { isDragOver = false },
                    onDragOver = { /* keep drag state */ },
                    onFileDrop = { files ->
                        isDragOver = false
                        if (files.isNotEmpty() && !uploading) {
                            uploading = true
                            uploadStatus = "Processing dropped file..."
                            corScope.launch {
                                vm.submitSolutionWithFile(
                                    assignmentId = assignment.id,
                                    file = files.first(),
                                    onStatus = { response ->
                                        uploadStatus = when (response.status) {
                                            org.jikvict.api.models.PendingStatusResponseLong.Status.PENDING -> "Processing..."
                                            org.jikvict.api.models.PendingStatusResponseLong.Status.DONE -> "Done"
                                            org.jikvict.api.models.PendingStatusResponseLong.Status.FAILED -> "Failed"
                                            org.jikvict.api.models.PendingStatusResponseLong.Status.REJECTED -> "Rejected"
                                        }

                                        if (response.status != org.jikvict.api.models.PendingStatusResponseLong.Status.PENDING) {
                                            when (response.status) {
                                                org.jikvict.api.models.PendingStatusResponseLong.Status.DONE -> {
                                                    showNotification(
                                                        "Task completed successfully!",
                                                        NotificationType.SUCCESS
                                                    )
                                                }

                                                org.jikvict.api.models.PendingStatusResponseLong.Status.FAILED -> {
                                                    val message = response.message ?: "Task failed"
                                                    showNotification("Task failed: $message", NotificationType.ERROR)
                                                }

                                                org.jikvict.api.models.PendingStatusResponseLong.Status.REJECTED -> {
                                                    val message = response.message ?: "Task was rejected"
                                                    showNotification("Task rejected: $message", NotificationType.ERROR)
                                                }

                                                else -> {}
                                            }
                                            uploading = false
                                        }
                                    },
                                    onFinished = { ok ->
                                        if (!ok) {
                                            uploadStatus = "Upload failed"
                                            uploading = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                )

                onDispose {
                    dragHandler?.cleanup()
                    dragHandler = null
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Download button
                ActionIconButton(
                    icon = Icons.Default.Download,
                    label = if (downloading) "Downloading..." else "Download",
                    enabled = !downloading,
                    onClick = {
                        downloading = true
                        corScope.launch {
                            vm.downloadZipAndSave(assignment.taskNumber) {
                                println("Result is: $it")
                                downloading = false
                            }
                        }
                    }
                )

                // Upload button (submits solution and polls status)
                ActionIconButton(
                    icon = Icons.Default.Upload,
                    label = if (uploading) uploadStatus.ifEmpty { "Uploading..." } else "Upload",
                    enabled = !uploading,
                    onClick = {
                        uploading = true
                        uploadStatus = "Picking file..."
                        corScope.launch {
                            vm.submitSolutionWithPicker(
                                assignmentId = assignment.id,
                                onStatus = { response ->
                                    uploadStatus = when (response.status) {
                                        org.jikvict.api.models.PendingStatusResponseLong.Status.PENDING -> "Processing..."
                                        org.jikvict.api.models.PendingStatusResponseLong.Status.DONE -> "Done"
                                        org.jikvict.api.models.PendingStatusResponseLong.Status.FAILED -> "Failed"
                                        org.jikvict.api.models.PendingStatusResponseLong.Status.REJECTED -> "Rejected"
                                    }

                                    // Show notifications for completed tasks
                                    if (response.status != org.jikvict.api.models.PendingStatusResponseLong.Status.PENDING) {
                                        when (response.status) {
                                            org.jikvict.api.models.PendingStatusResponseLong.Status.DONE -> {
                                                showNotification(
                                                    "Task completed successfully!",
                                                    NotificationType.SUCCESS
                                                )
                                            }

                                            org.jikvict.api.models.PendingStatusResponseLong.Status.FAILED -> {
                                                val message = response.message ?: "Task failed"
                                                showNotification("Task failed: $message", NotificationType.ERROR)
                                            }

                                            org.jikvict.api.models.PendingStatusResponseLong.Status.REJECTED -> {
                                                val message = response.message ?: "Task was rejected"
                                                showNotification("Task rejected: $message", NotificationType.ERROR)
                                            }

                                            else -> {}
                                        }
                                        // allow user to re-upload after completion
                                        uploading = false
                                    }
                                },
                                onFinished = { ok ->
                                    if (!ok) {
                                        uploadStatus = "Upload canceled or failed"
                                        uploading = false
                                    } else if (uploadStatus.isEmpty()) {
                                        uploadStatus = "Processing..."
                                    }
                                }
                            )
                        }
                    }
                )
            }

            Box(
                modifier = Modifier.weight(1f).fillMaxSize().then(
                    if (isDragOver) {
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    } else {
                        Modifier
                    }
                ),
                contentAlignment = Alignment.Center,
            ) {
                if (isDragOver) {
                    Icon(
                        Icons.Rounded.FileUpload,
                        contentDescription = "Drag and drop file here",
                        modifier = Modifier.fillMaxSize(0.5f),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

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


    val iconSize = 28.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val animatedTint by animateColorAsState(
        targetValue = if (isHovered) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.primary
        },
        label = "back_icon_tint"
    )


    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.Transparent, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isRefreshing
            ) {
                onClick()
            }
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh",
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    rotationZ = rotation
                },
            tint = animatedTint,
        )
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ActionIconButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val animatedTint by animateColorAsState(
        targetValue = if (isHovered) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.primary
        },
        label = "action_icon_tint"
    )
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "action_icon_alpha",
    )
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "action_icon_scale",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha; this.scaleX = scale; this.scaleY = scale }
            .background(Color.Transparent, CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = animatedTint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
private fun TaskNotificationOverlay(
    notification: TaskNotification,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = notification.isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
                    .clickable { onDismiss() },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (notification.type) {
                        NotificationType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                        NotificationType.ERROR -> MaterialTheme.colorScheme.errorContainer
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val iconFont = rememberStaticIconFont(
                        fontResource = Res.font.`MaterialSymbolsOutlined_VariableFont_FILL,GRAD,opsz,wght`
                    )

                    // Icon based on notification type
                    FontIcon(
                        iconFont = iconFont,
                        icon = when (notification.type) {
                            NotificationType.SUCCESS -> '\ue876' // check_circle
                            NotificationType.ERROR -> '\ue000' // error
                        },
                        contentDescription = when (notification.type) {
                            NotificationType.SUCCESS -> "Success"
                            NotificationType.ERROR -> "Error"
                        },
                        tint = when (notification.type) {
                            NotificationType.SUCCESS -> MaterialTheme.colorScheme.primary
                            NotificationType.ERROR -> MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (notification.type) {
                            NotificationType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
                            NotificationType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Close button
                    FontIcon(
                        iconFont = iconFont,
                        icon = '\ue5cd', // close
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDismiss() }
                    )
                }
            }
        }
    }
}
