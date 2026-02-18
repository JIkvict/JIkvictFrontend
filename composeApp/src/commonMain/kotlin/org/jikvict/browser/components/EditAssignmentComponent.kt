package org.jikvict.browser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.responsive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

sealed class EditAssignmentState {
    object Idle : EditAssignmentState()
    object Loading : EditAssignmentState()
    data class Success(val assignment: AssignmentDto) : EditAssignmentState()
    data class Error(val message: String) : EditAssignmentState()
}

sealed class DeleteAssignmentState {
    object Idle : DeleteAssignmentState()
    object Confirming : DeleteAssignmentState()
    object Loading : DeleteAssignmentState()
    object Success : DeleteAssignmentState()
    data class Error(val message: String) : DeleteAssignmentState()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EditAssignmentComponent(
    scope: DefaultScreenScope,
    onNavigateBack: () -> Unit,
    assignment: AssignmentDto,
    availableAssignmentGroups: List<AssignmentGroupDto>,
    availableTasks: List<Long>,
    onUpdate: suspend (AssignmentDto) -> OperationResult<AssignmentDto>,
    onNavigateToUpdated: (AssignmentDto) -> Unit = {},
    onDelete: suspend () -> OperationResult<Unit> = { OperationResult.Error("Not implemented") },
) {
    var editState by remember { mutableStateOf<EditAssignmentState>(EditAssignmentState.Idle) }
    var deleteState by remember { mutableStateOf<DeleteAssignmentState>(DeleteAssignmentState.Idle) }
    var title by remember { mutableStateOf(assignment.title) }
    var taskId by remember { mutableStateOf<Long?>(assignment.taskId.toLong()) }
    var maxPoints by remember { mutableStateOf(assignment.maxPoints.toString()) }

    var startDate by remember {
        mutableStateOf(
            LocalDateTime.parse(assignment.startDate)
        )
    }
    var endDate by remember {
        mutableStateOf(
            LocalDateTime.parse(assignment.endDate)
        )
    }
    // Calculate initial units and values
    val initialTimeout = assignment.timeOutSeconds
    val initialTimeoutUnit = when {
        initialTimeout % 1 == 0L -> TimeUnit.SECONDS
        else -> TimeUnit.MILLISECONDS
    }
    var timeoutValue by remember {
        mutableStateOf((initialTimeout / (initialTimeoutUnit.nanosMultiplier / 1000000000)).toString())
    }
    var timeoutUnit by remember { mutableStateOf(initialTimeoutUnit) }

    val initialMemory = assignment.memoryLimit
    val initialMemoryUnit = when {
        initialMemory >= MemoryUnit.GB.bytesMultiplier && initialMemory % MemoryUnit.GB.bytesMultiplier == 0L -> MemoryUnit.GB
        initialMemory >= MemoryUnit.MB.bytesMultiplier && initialMemory % MemoryUnit.MB.bytesMultiplier == 0L -> MemoryUnit.MB
        initialMemory >= MemoryUnit.KB.bytesMultiplier && initialMemory % MemoryUnit.KB.bytesMultiplier == 0L -> MemoryUnit.KB
        else -> MemoryUnit.B
    }
    var memoryValue by remember {
        mutableStateOf((initialMemory / initialMemoryUnit.bytesMultiplier).toString())
    }
    var memoryUnit by remember { mutableStateOf(initialMemoryUnit) }

    val initialCpu = assignment.cpuLimit
    val initialCpuUnit = when {
        initialCpu >= CpuUnit.CPU.nanoCpuMultiplier && initialCpu % CpuUnit.CPU.nanoCpuMultiplier == 0L -> CpuUnit.CPU
        initialCpu >= CpuUnit.MILLI_CPU.nanoCpuMultiplier && initialCpu % CpuUnit.MILLI_CPU.nanoCpuMultiplier == 0L -> CpuUnit.MILLI_CPU
        else -> CpuUnit.NANO_CPU
    }
    var cpuValue by remember {
        mutableStateOf((initialCpu / initialCpuUnit.nanoCpuMultiplier).toString())
    }
    var cpuUnit by remember { mutableStateOf(initialCpuUnit) }

    var pidsLimit by remember { mutableStateOf(assignment.pidsLimit.toString()) }
    var maxAttempts by remember { mutableStateOf(assignment.maximumAttempts.toString()) }

    var selectedGroupIds by remember { mutableStateOf(assignment.assignmentGroupsIds) }

    var taskSearchQuery by remember { mutableStateOf("") }
    var groupSearchQuery by remember { mutableStateOf("") }
    var showTaskDropdown by remember { mutableStateOf(false) }
    var showGroupDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (editState !is EditAssignmentState.Idle) {
        Dialog(
            onDismissRequest = {
                if (editState !is EditAssignmentState.Loading) {
                    editState = EditAssignmentState.Idle
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = editState !is EditAssignmentState.Loading,
                dismissOnClickOutside = editState !is EditAssignmentState.Loading
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (val state = editState) {
                        is EditAssignmentState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Updating assignment...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        is EditAssignmentState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Assignment updated successfully!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        editState = EditAssignmentState.Idle
                                        onNavigateBack()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Back")
                                }
                                Button(
                                    onClick = { onNavigateToUpdated(state.assignment) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Go to Assignment")
                                }
                            }
                        }

                        is EditAssignmentState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Error updating assignment",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { editState = EditAssignmentState.Idle },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Try Again")
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    if (deleteState !is DeleteAssignmentState.Idle) {
        Dialog(
            onDismissRequest = {
                if (deleteState is DeleteAssignmentState.Confirming || deleteState is DeleteAssignmentState.Error) {
                    deleteState = DeleteAssignmentState.Idle
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = deleteState !is DeleteAssignmentState.Loading,
                dismissOnClickOutside = deleteState !is DeleteAssignmentState.Loading
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (deleteState) {
                        is DeleteAssignmentState.Confirming -> {
                            Text(
                                text = "Delete Assignment?",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Are you sure you want to delete \"${assignment.title}\"? This action cannot be undone.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { deleteState = DeleteAssignmentState.Idle },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Cancel") }
                                Button(
                                    onClick = {
                                        deleteState = DeleteAssignmentState.Loading
                                        coroutineScope.launch {
                                            val result = onDelete()
                                            deleteState = when (result) {
                                                is OperationResult.Success -> DeleteAssignmentState.Success
                                                is OperationResult.Error -> DeleteAssignmentState.Error(result.message)
                                                else -> DeleteAssignmentState.Idle
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) { Text("Delete") }
                            }
                        }

                        is DeleteAssignmentState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Deleting assignment...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        is DeleteAssignmentState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Assignment deleted successfully!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(
                                onClick = {
                                    deleteState = DeleteAssignmentState.Idle
                                    onNavigateBack()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Back") }
                        }

                        is DeleteAssignmentState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Error deleting assignment",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = (deleteState as DeleteAssignmentState.Error).message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { deleteState = DeleteAssignmentState.Idle },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Close") }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    if (showStartDatePicker) {
        DateTimePickerDialog(
            initialDateTime = startDate,
            onDismiss = { showStartDatePicker = false },
            onConfirm = { newDate ->
                startDate = newDate
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        DateTimePickerDialog(
            initialDateTime = endDate,
            onDismiss = { showEndDatePicker = false },
            onConfirm = { newDate ->
                endDate = newDate
                showEndDatePicker = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth().heightIn(scope.screenHeight).padding(vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            NavigateBackButton(
                onNavigateBack = onNavigateBack, title = "Back"
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.responsive(mainColumnModifier),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AssignmentFormCard(
                    cardTitle = if (scope.isReadOnly) "Assignment Info" else "Edit Assignment",
                    title = title,
                    onTitleChange = { title = it },
                    taskId = taskId,
                    onTaskIdChange = { taskId = it },
                    taskSearchQuery = taskSearchQuery,
                    onTaskSearchQueryChange = { taskSearchQuery = it },
                    showTaskDropdown = showTaskDropdown,
                    onShowTaskDropdownChange = { showTaskDropdown = it },
                    availableTasks = availableTasks,
                    maxPoints = maxPoints,
                    onMaxPointsChange = { maxPoints = it },
                    startDate = startDate,
                    onStartDateClick = { showStartDatePicker = true },
                    endDate = endDate,
                    onEndDateClick = { showEndDatePicker = true },
                    timeoutValue = timeoutValue,
                    onTimeoutValueChange = { timeoutValue = it },
                    timeoutUnit = timeoutUnit,
                    onTimeoutUnitChange = { timeoutUnit = it },
                    memoryValue = memoryValue,
                    onMemoryValueChange = { memoryValue = it },
                    memoryUnit = memoryUnit,
                    onMemoryUnitChange = { memoryUnit = it },
                    cpuValue = cpuValue,
                    onCpuValueChange = { cpuValue = it },
                    cpuUnit = cpuUnit,
                    onCpuUnitChange = { cpuUnit = it },
                    pidsLimit = pidsLimit,
                    onPidsLimitChange = { pidsLimit = it },
                    maxAttempts = maxAttempts,
                    onMaxAttemptsChange = { maxAttempts = it },
                    groupSearchQuery = groupSearchQuery,
                    onGroupSearchQueryChange = { groupSearchQuery = it },
                    showGroupDropdown = showGroupDropdown,
                    onShowGroupDropdownChange = { showGroupDropdown = it },
                    availableAssignmentGroups = availableAssignmentGroups,
                    selectedGroupIds = selectedGroupIds,
                    onSelectedGroupIdsChange = { selectedGroupIds = it },
                    onCancel = onNavigateBack,
                    onSubmit = {
                        editState = EditAssignmentState.Loading
                        coroutineScope.launch {
                            val result = onUpdate(
                                AssignmentDto(
                                    id = assignment.id,
                                    title = title,
                                    taskId = taskId!!.toInt(),
                                    maxPoints = maxPoints.toIntOrNull() ?: 100,
                                    startDate = startDate.toString(),
                                    endDate = endDate.toString(),
                                    timeOutSeconds = (timeoutValue.toLongOrNull()
                                        ?: 0) * timeoutUnit.nanosMultiplier / 1000000000,
                                    memoryLimit = (memoryValue.toLongOrNull()
                                        ?: 0) * memoryUnit.bytesMultiplier,
                                    cpuLimit = (cpuValue.toLongOrNull()
                                        ?: 0) * cpuUnit.nanoCpuMultiplier,
                                    pidsLimit = pidsLimit.toLongOrNull() ?: 20,
                                    isClosed = assignment.isClosed,
                                    maximumAttempts = maxAttempts.toIntOrNull() ?: 1,
                                    assignmentGroupsIds = selectedGroupIds
                                )
                            )
                            editState = when (result) {
                                is OperationResult.Success -> EditAssignmentState.Success(result.result)
                                is OperationResult.Error -> EditAssignmentState.Error(result.message)
                                is OperationResult.Loading -> EditAssignmentState.Loading
                                is OperationResult.Idle -> EditAssignmentState.Idle
                            }
                        }
                    },
                    submitButtonText = "Save",
                    isSubmitEnabled = title.isNotBlank() && taskId != null && editState is EditAssignmentState.Idle,
                    isReadOnly = scope.isReadOnly
                )

                if (!scope.isReadOnly) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { deleteState = DeleteAssignmentState.Confirming },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = deleteState is DeleteAssignmentState.Idle
                    ) { Text("Delete") }
                }
            }
        }
    }
}


@OptIn(ExperimentalTime::class)
@Preview(widthDp = 1980, heightDp = 1080)
@Composable
fun EditAssignmentComponentPreview() {
    DefaultPreview {
        val now = Clock.System.now()
        EditAssignmentComponent(
            it,
            onNavigateBack = {},
            assignment = AssignmentDto(
                id = 2247,
                title = "posse",
                taskId = 8784,
                maxPoints = 8976,
                startDate = now.toString(),
                endDate = now.toString(),
                timeOutSeconds = 2943,
                memoryLimit = 1423,
                cpuLimit = 5304,
                pidsLimit = 6929,
                isClosed = false,
                maximumAttempts = 3798,
                assignmentGroupsIds = listOf(),
                description = "atomorum"

            ),
            availableAssignmentGroups = listOf(
                AssignmentGroupDto(
                    name = "Some group",
                    userIds = listOf(1),
                    assignmentIds = listOf(1),
                    id = 1
                )
            ),
            availableTasks = listOf(1),
            onUpdate = { OperationResult.Loading() }
        )
    }
}

