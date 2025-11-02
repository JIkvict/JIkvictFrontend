package org.jikvict.browser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.responsive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

sealed class EditAssignmentState {
    object Idle : EditAssignmentState()
    object Loading : EditAssignmentState()
    object Success : EditAssignmentState()
    data class Error(val message: String) : EditAssignmentState()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EditAssignmentComponent(
    scope: DefaultScreenScope,
    onNavigateBack: () -> Unit,
    assignment: AssignmentDto,
    availableAssignmentGroups: List<AssignmentGroupDto>,
    availableTasks: List<Long>,
    onUpdate: suspend (AssignmentDto) -> OperationResult<AssignmentDto>
) {
    var editState by remember { mutableStateOf<EditAssignmentState>(EditAssignmentState.Idle) }
    var title by remember { mutableStateOf(assignment.title) }
    var taskId by remember { mutableStateOf<Long?>(assignment.taskId.toLong()) }
    var maxPoints by remember { mutableStateOf(assignment.maxPoints.toString()) }

    var startDate by remember {
        mutableStateOf(
            Instant.parse(assignment.startDate).toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }
    var endDate by remember {
        mutableStateOf(
            Instant.parse(assignment.endDate).toLocalDateTime(TimeZone.currentSystemDefault())
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

    with(scope) {
        Box(
            modifier = Modifier.fitContentToScreen(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))

                NavigateBackButton(
                    onNavigateBack = onNavigateBack,
                    title = "Assignments"
                )

                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyColumn(
                        modifier = Modifier.responsive(mainColumnModifier),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            AssignmentFormCard(
                                cardTitle = "Edit Assignment",
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
                                            is OperationResult.Success -> EditAssignmentState.Success
                                            is OperationResult.Error -> EditAssignmentState.Error(result.message)
                                            is OperationResult.Loading -> EditAssignmentState.Loading
                                        }
                                    }
                                },
                                submitButtonText = "Save",
                                isSubmitEnabled = title.isNotBlank() && taskId != null && editState is EditAssignmentState.Idle
                            )
                        }
                    }
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

