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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import dev.tclement.fonticons.FontIcon
import dev.tclement.fonticons.rememberStaticIconFont
import jikvictfrontend.composeapp.generated.resources.MaterialSymbolsOutlined_VariableFont
import jikvictfrontend.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentInfo
import org.jikvict.api.models.PendingStatusResponseLong
import org.jikvict.api.models.TestResult
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.icons.MyIconPack
import org.jikvict.browser.icons.myiconpack.Lockeddark
import org.jikvict.browser.icons.myiconpack.Lockedlight
import org.jikvict.browser.icons.myiconpack.Taskstatusdark
import org.jikvict.browser.icons.myiconpack.Taskstatusdonedark
import org.jikvict.browser.icons.myiconpack.Taskstatusdonelight
import org.jikvict.browser.icons.myiconpack.Taskstatusfaileddark
import org.jikvict.browser.icons.myiconpack.Taskstatusfailedlight
import org.jikvict.browser.icons.myiconpack.Taskstatuslight
import org.jikvict.browser.icons.myiconpack.Unlockeddark
import org.jikvict.browser.icons.myiconpack.Unlockedlight
import org.jikvict.browser.util.DragDropHandler
import org.jikvict.browser.util.LocalThemeSwitcherProvider
import org.jikvict.browser.util.SimplePreview
import org.jikvict.browser.util.setupDragAndDropHandlers
import org.jikvict.browser.viewmodel.SubmissionStatus
import org.jikvict.browser.viewmodel.TasksScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

// Helper functions for formatting values in human-readable format
@OptIn(kotlin.time.ExperimentalTime::class)
private fun formatMemory(memoryB: Long): String {
    return "" + (memoryB / 1024 / 1024)
}


private fun formatCpuLimit(cpuLimit: Long): String {
    return "" + (cpuLimit.toDouble() / 1e9)
}

private fun formatDate(dateString: String): String {
    return try {
        val parts = dateString.replace("T", " ").replace("Z", "").split(":")
        if (parts.size >= 2) {
            "${parts[0]}:${parts[1]}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}


data class TaskNotification(
    val message: String,
    val type: NotificationType,
    val isVisible: Boolean = true,
)

enum class NotificationType {
    SUCCESS,
    ERROR,
}

sealed class AssignmentsUiState {
    data object Loading : AssignmentsUiState()

    data class Success(
        val assignments: List<AssignmentDto>,
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
        val assignmentInfos by viewModel.assignmentInfoMap.collectAsState()

        var selectedAssignmentId by remember { mutableLongStateOf(-1L) }
        var notification by remember { mutableStateOf<TaskNotification?>(null) }

        fun refreshAssignments() {
            viewModel.refreshAssignments()
        }

        fun showNotification(
            message: String,
            type: NotificationType,
        ) {
            println("Showing notification: $message, $type")
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

        val selectedAssignment: AssignmentDto? =
            remember(selectedAssignmentId, assignments) {
                if (selectedAssignmentId != -1L) {
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
                                                viewModel.resetLogsState()
                                                scope.launch {
                                                    navigator.navigateTo(
                                                        ListDetailPaneScaffoldRole.Detail,
                                                        assignment.id.toInt(),
                                                    )
                                                }
                                            },
                                            assignmentInfos = assignmentInfos
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
                            showNotification = ::showNotification,
                            assignmentInfo = assignmentInfos[assignment.id.toInt()]
                        )
                    } ?: EmptyDetailPane()
                },
                modifier = Modifier.fitContentToScreen(),
            )
        }

        notification?.let { notif ->
            TaskNotificationOverlay(
                notification = notif,
                onDismiss = { hideNotification() },
            )
        }
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AssignmentListPane(
    assignments: List<AssignmentDto>,
    onAssignmentClick: (AssignmentDto) -> Unit,
    assignmentInfos: Map<Int, AssignmentInfo> = emptyMap(),
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
                    assignmentInfo = assignmentInfos[assignment.id.toInt()],
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AssignmentListItem(
    assignment: AssignmentDto = AssignmentDto(
        id = 1,
        title = "Some title",
        taskId = 1,
        maxPoints = 20,
        startDate = "2023-01-01T00:00:00Z",
        endDate = "2023-01-01T00:00:00Z",
        timeOutSeconds = 500,
        memoryLimit = 500,
        cpuLimit = 500,
        pidsLimit = 500,
        isClosed = false,
        maximumAttempts = 3
    ),
    onClick: () -> Unit = {},
    assignmentInfo: AssignmentInfo? = null,
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
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = assignment.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Task #${assignment.taskId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (assignmentInfo != null) {
                Box(
                    modifier = Modifier.size(24.dp)
                ) {
                    TaskStatusIcon(assignmentInfo = assignmentInfo, assignment = assignment)
                }
            }
        }
    }
}


@Composable
fun TaskStatusIcon(assignmentInfo: AssignmentInfo, assignment: AssignmentDto) {
    val modifier = Modifier.fillMaxSize()
    when (SubmissionStatus.from(assignmentInfo, assignment)) {
        SubmissionStatus.PASSED -> TaskPassed(modifier)
        SubmissionStatus.FAILED -> TaskIsFailed(modifier)
        SubmissionStatus.CLOSED -> TaskIsClosed(modifier)
        SubmissionStatus.OPEN -> TaskIsOpen(modifier)
    }
}

@Composable
@Preview
fun AssignmentListItemPreview() {
    SimplePreview(isDark = false) {
        AssignmentListItem()
    }
}

@Composable
private fun TaskPassed(modifier: Modifier = Modifier) {
    val theme = LocalThemeSwitcherProvider.current
    val isDark = theme.isDark
    val imageVector =
        if (isDark.value) {
            MyIconPack.Taskstatusdonedark
        } else {
            MyIconPack.Taskstatusdonelight
        }
    Icon(
        imageVector = imageVector,
        contentDescription = "Passed",
        modifier = modifier,
        tint = Color.Unspecified,
    )
}

@Composable
private fun TaskIsOpen(modifier: Modifier = Modifier) {
    val theme = LocalThemeSwitcherProvider.current
    val isDark = theme.isDark
    val imageVector =
        if (isDark.value) {
            MyIconPack.Taskstatusdark
        } else {
            MyIconPack.Taskstatuslight
        }
    Icon(
        imageVector = imageVector,
        contentDescription = "Passed",
        modifier = modifier,
        tint = Color.Unspecified,
    )
}

@Composable
private fun TaskIsFailed(modifier: Modifier = Modifier) {
    val theme = LocalThemeSwitcherProvider.current
    val isDark = theme.isDark
    val imageVector =
        if (isDark.value) {
            MyIconPack.Taskstatusfaileddark
        } else {
            MyIconPack.Taskstatusfailedlight
        }
    Icon(
        imageVector = imageVector,
        contentDescription = "Passed",
        modifier = modifier,
        tint = Color.Unspecified
    )
}

@Composable
private fun TaskIsClosed(modifier: Modifier = Modifier) {
    val theme = LocalThemeSwitcherProvider.current
    val isDark = theme.isDark
    val imageVector =
        if (isDark.value) {
            MyIconPack.Lockeddark
        } else {
            MyIconPack.Lockedlight
        }
    Icon(
        imageVector = imageVector,
        contentDescription = "Closed",
        modifier = modifier,
        tint = Color.Unspecified
    )
}

@Preview
@Composable
private fun PreviewIcons() {
    SimplePreview {
        Row {
            TaskIsOpen(Modifier.size(24.dp))
            Spacer(modifier = Modifier.weight(1f))
            TaskIsFailed(Modifier.size(24.dp))
            Spacer(modifier = Modifier.weight(1f))
            TaskPassed(Modifier.size(24.dp))
            Spacer(modifier = Modifier.weight(1f))
            TaskIsClosed(Modifier.size(24.dp))
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
context(scope: DefaultScreenScope)
private fun AssignmentDetailPane(
    assignment: AssignmentDto,
    navigator: ThreePaneScaffoldNavigator<Int>,
    showNotification: (String, NotificationType) -> Unit = { _, _ -> },
    assignmentInfo: AssignmentInfo? = null,
) {
    val scrollState = rememberScrollState()
    val theme = LocalThemeSwitcherProvider.current
    val isDark by theme.isDark

    // Get ViewModel and state
    val vm = koinViewModel<TasksScreenViewModel>()
    val showLogs by vm.showLogs.collectAsState()
    val selectedAttemptIndex by vm.selectedAttemptIndex.collectAsState()

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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Task Details",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                val scope = rememberCoroutineScope()
                val iconFont =
                    rememberStaticIconFont(
                        fontResource = Res.font.MaterialSymbolsOutlined_VariableFont,
                    )
                navigator.let { nav ->
                    if (nav.canNavigateBack()) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val isHovered by interactionSource.collectIsHoveredAsState()

                        val animatedTint by animateColorAsState(
                            targetValue =
                                if (isHovered) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                            label = "back_icon_tint",
                        )

                        Box(
                            modifier =
                                Modifier.clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                ) {
                                    scope.launch {
                                        nav.navigateBack()
                                    }
                                },
                        ) {
                            FontIcon(
                                iconFont = iconFont,
                                icon = '\uf6ff',
                                contentDescription = "Back",
                                tint = animatedTint,
                                modifier =
                                    Modifier.size(32.dp).graphicsLayer {
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

            if (showLogs && assignmentInfo != null && assignmentInfo.results.isNotEmpty()) {
                LogsDisplayContainer(
                    assignmentInfo = assignmentInfo,
                    selectedAttemptIndex = selectedAttemptIndex,
                    onAttemptChange = { vm.setSelectedAttemptIndex(it) },
                    onBackClick = { vm.setShowLogs(false) },
                    modifier = Modifier.fillMaxWidth().heightIn(max = scope.screenHeight * 0.7f)
                )
            } else {
                OutlinedContentContainer(label = "Description") {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = scope.screenHeight * 0.5f),
                    ) {
                        item {
                            Markdown(assignment.description!!)
                        }
                    }
                }
            }

            val greenColor = if (isDark) {
                DarkColors.Green6
            } else {
                LightColors.Green4
            }


            if (assignment.isClosed && !showLogs) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(onClick = {
                        vm.setShowLogs(true)
                        vm.setSelectedAttemptIndex(0)
                    }) {
                        Text(
                            text = "See logs",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        val imageVector =
                            if (isDark) {
                                MyIconPack.Unlockeddark
                            } else {
                                MyIconPack.Unlockedlight
                            }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = imageVector,
                            contentDescription = "Unlocked",
                            modifier = Modifier.size(24.dp),
                        )

                    }
                }
            }

            val points = assignmentInfo?.results?.maxOfOrNull { it.points } ?: 0
            Text(
                text = "$points/${assignment.maxPoints} points ~ ${
                    (points * 100 / assignment.maxPoints.toDouble().coerceAtLeast(1.0)).toInt()
                }%",
                style = MaterialTheme.typography.labelLarge,
                color = greenColor
            )

            Text(
                text = "${assignmentInfo?.attemptsUsed ?: 0}/${assignment.maximumAttempts} attempts",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )


            // Start and End dates
            Text(
                text = "Start: ${formatDate(assignment.startDate)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "End: ${formatDate(assignment.endDate)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Task #${assignment.taskId}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
            )

            Text(
                text = "Timeout: ${assignment.timeOutSeconds} s | Memory (RAM): ${formatMemory(assignment.memoryLimit)} MB",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            )

            Text(
                text = "CPU Limit: ${formatCpuLimit(assignment.cpuLimit)} cores | PIDs: ${assignment.pidsLimit} processes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
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

            if ((assignmentInfo == null || SubmissionStatus.from(
                    assignmentInfo,
                    assignment
                ) == SubmissionStatus.OPEN)
            ) {

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
                                    uploadStatus = "Processing dropped file..."
                                    corScope.launch {
                                        vm.submitSolutionWithFile(
                                            assignmentId = assignment.id.toInt(),
                                            file = files.first(),
                                            onStatus = { response ->
                                                uploadStatus =
                                                    when (response.status) {
                                                        PendingStatusResponseLong.Status.PENDING -> "Processing..."
                                                        PendingStatusResponseLong.Status.DONE -> "Done"
                                                        PendingStatusResponseLong.Status.FAILED -> "Failed"
                                                        PendingStatusResponseLong.Status.REJECTED -> "Rejected"
                                                    }

                                                if (response.status != PendingStatusResponseLong.Status.PENDING) {
                                                    when (response.status) {
                                                        PendingStatusResponseLong.Status.DONE -> {
                                                            showNotification(
                                                                "Task completed successfully!",
                                                                NotificationType.SUCCESS,
                                                            )
                                                        }

                                                        PendingStatusResponseLong.Status.FAILED -> {
                                                            val message = response.message ?: "Task failed"
                                                            showNotification(
                                                                "Task failed: $message",
                                                                NotificationType.ERROR
                                                            )
                                                        }

                                                        PendingStatusResponseLong.Status.REJECTED -> {
                                                            val message = response.message ?: "Task was rejected"
                                                            showNotification(
                                                                "Task rejected: $message",
                                                                NotificationType.ERROR
                                                            )
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
                                            },
                                            onError = { error ->
                                                uploadStatus = error
                                                showNotification(error, NotificationType.ERROR)
                                                uploading = false
                                            }
                                        )
                                    }
                                }
                            },
                        )

                    onDispose {
                        dragHandler?.cleanup()
                        dragHandler = null
                    }
                }
            }


            if ((assignmentInfo == null || SubmissionStatus.from(
                    assignmentInfo,
                    assignment
                ) == SubmissionStatus.OPEN)
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Download button
                    ActionIconButton(
                        icon = Icons.Default.Download,
                        label = if (downloading) "Downloading..." else "Download",
                        enabled = !downloading,
                        onClick = {
                            downloading = true
                            corScope.launch {
                                vm.downloadZipAndSave(assignment.id.toInt()) {
                                    println("Result is: $it")
                                    downloading = false
                                }
                            }
                        },
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
                                    assignmentId = assignment.id.toInt(),
                                    onStatus = { response ->
                                        uploadStatus =
                                            when (response.status) {
                                                PendingStatusResponseLong.Status.PENDING -> "Processing..."
                                                PendingStatusResponseLong.Status.DONE -> "Done"
                                                PendingStatusResponseLong.Status.FAILED -> "Failed"
                                                PendingStatusResponseLong.Status.REJECTED -> "Rejected"
                                            }

                                        // Show notifications for completed tasks
                                        if (response.status != PendingStatusResponseLong.Status.PENDING) {
                                            when (response.status) {
                                                PendingStatusResponseLong.Status.DONE -> {
                                                    showNotification(
                                                        "Task completed successfully!",
                                                        NotificationType.SUCCESS,
                                                    )
                                                }

                                                PendingStatusResponseLong.Status.FAILED -> {
                                                    val message = response.message ?: "Task failed"
                                                    showNotification("Task failed: $message", NotificationType.ERROR)
                                                }

                                                PendingStatusResponseLong.Status.REJECTED -> {
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
                                    },
                                    onError = { error ->
                                        uploadStatus = error
                                        showNotification(error, NotificationType.ERROR)
                                        uploading = false
                                    }
                                )
                            }
                        },
                    )
                }
            }
            Box(
                modifier =
                    Modifier.weight(1f).fillMaxSize().then(
                        if (isDragOver) {
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp),
                                ).border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier
                        },
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

@Composable
private fun LogsDisplayContainer(
    assignmentInfo: AssignmentInfo,
    selectedAttemptIndex: Int,
    onAttemptChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalThemeSwitcherProvider.current
    val isDark by theme.isDark

    Column(modifier = modifier) {
        // Header with back button and attempt navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back")
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (assignmentInfo.results.size > 1) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(assignmentInfo.results) { index, _ ->
                            OutlinedButton(
                                onClick = { onAttemptChange(index) },
                                colors = if (index == selectedAttemptIndex) {
                                    androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                } else {
                                    androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                                }
                            ) {
                                Text("${index + 1}")
                            }
                        }
                    }
                }
            }
        }

        // Display selected attempt details
        val selectedResult = assignmentInfo.results.getOrNull(selectedAttemptIndex)
        if (selectedResult != null) {
            OutlinedContentContainer(
                label = "Attempt ${selectedAttemptIndex + 1} - ${selectedResult.timeStamp} (${selectedResult.points} points)"
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedResult.result?.testResults?.let { testResults ->
                        items(testResults) { testResult ->
                            var isExpanded by remember { mutableStateOf(false) }
                            TestResultCard(
                                testResult = testResult,
                                isDark = isDark,
                                isExpanded = isExpanded,
                                onClick = { isExpanded = !isExpanded }
                            )
                        }
                    } ?: run {
                        item {
                            Text(
                                text = "No detailed test results available for this attempt.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestResultCard(
    testResult: TestResult,
    isDark: Boolean,
    isExpanded: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (testResult.passed) {
                if (isDark) Color(0xFF1B5E20) else Color(0xFFE8F5E8)
            } else {
                if (isDark) Color(0xFF5F2120) else Color(0xFFFFEBEE)
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = testResult.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = testResult.testName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${testResult.earnedPoints}/${testResult.possiblePoints}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (testResult.passed) {
                            if (isDark) Color(0xFF4CAF50) else Color(0xFF2E7D32)
                        } else {
                            if (isDark) Color(0xFFEF5350) else Color(0xFFC62828)
                        }
                    )
                    Text(
                        text = if (testResult.passed) "PASSED" else "FAILED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (testResult.passed) {
                            if (isDark) Color(0xFF4CAF50) else Color(0xFF2E7D32)
                        } else {
                            if (isDark) Color(0xFFEF5350) else Color(0xFFC62828)
                        }
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Logs:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (testResult.logs.isNotEmpty()) {
                            testResult.logs.forEach { log ->
                                Text(
                                    text = log,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Text(
                                text = "No logs found for this test",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
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
        targetValue =
            if (isHovered) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.primary
            },
        label = "back_icon_tint",
    )

    Box(
        modifier =
            Modifier
                .padding(16.dp)
                .background(Color.Transparent, CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isRefreshing,
                ) {
                    onClick()
                },
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
        targetValue =
            if (isHovered) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.primary
            },
        label = "action_icon_tint",
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
        modifier =
            Modifier
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                }.background(Color.Transparent, CircleShape)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).padding(vertical = 8.dp, horizontal = 12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = animatedTint,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun TaskNotificationOverlay(
    notification: TaskNotification,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = notification.isVisible,
        enter =
            slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300),
            ) + fadeIn(animationSpec = tween(300)),
        exit =
            slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300),
            ) + fadeOut(animationSpec = tween(300)),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Card(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.9f)
                        .clickable { onDismiss() },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            when (notification.type) {
                                NotificationType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                                NotificationType.ERROR -> MaterialTheme.colorScheme.errorContainer
                            },
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val iconFont =
                        rememberStaticIconFont(
                            fontResource = Res.font.MaterialSymbolsOutlined_VariableFont,
                        )

                    // Icon based on notification type
                    FontIcon(
                        iconFont = iconFont,
                        icon =
                            when (notification.type) {
                                NotificationType.SUCCESS -> '\ue876' // check_circle
                                NotificationType.ERROR -> '\ue000' // error
                            },
                        contentDescription =
                            when (notification.type) {
                                NotificationType.SUCCESS -> "Success"
                                NotificationType.ERROR -> "Error"
                            },
                        tint =
                            when (notification.type) {
                                NotificationType.SUCCESS -> MaterialTheme.colorScheme.primary
                                NotificationType.ERROR -> MaterialTheme.colorScheme.error
                            },
                        modifier = Modifier.size(24.dp),
                    )

                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                            when (notification.type) {
                                NotificationType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
                                NotificationType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                            },
                        modifier = Modifier.weight(1f),
                    )

                    // Close button
                    FontIcon(
                        iconFont = iconFont,
                        icon = '\ue5cd', // close
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clickable { onDismiss() },
                    )
                }
            }
        }
    }
}
