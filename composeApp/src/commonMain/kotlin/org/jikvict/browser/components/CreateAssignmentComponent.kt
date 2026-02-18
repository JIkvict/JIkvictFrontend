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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.CreateAssignmentDto
import org.jikvict.browser.components.common.SearchableDropdown
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.responsive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class MemoryUnit(val displayName: String, val bytesMultiplier: Long) {
    B("Bytes", 1L),
    KB("KB", 1024L),
    MB("MB", 1024L * 1024L),
    GB("GB", 1024L * 1024L * 1024L)
}

enum class TimeUnit(val displayName: String, val nanosMultiplier: Long) {
    NANOSECONDS("ns", 1L),
    MICROSECONDS("μs", 1000L),
    MILLISECONDS("ms", 1000000L),
    SECONDS("s", 1000000000L)
}

enum class CpuUnit(val displayName: String, val nanoCpuMultiplier: Long) {
    NANO_CPU("nanoCPU", 1L),
    MILLI_CPU("milliCPU", 1000000L),
    CPU("CPU", 1000000000L)
}

sealed class CreateAssignmentState {
    object Idle : CreateAssignmentState()
    object Loading : CreateAssignmentState()
    object Success : CreateAssignmentState()
    data class Error(val message: String) : CreateAssignmentState()
}

@OptIn(ExperimentalTime::class)
@Composable
fun CreateAssignmentComponent(
    scope: DefaultScreenScope,
    onNavigateBack: () -> Unit,
    availableAssignmentGroups: List<AssignmentGroupDto>,
    availableTasks: List<Long>,
    onCreate: suspend (CreateAssignmentDto) -> OperationResult<AssignmentDto>
) {
    var createState by remember { mutableStateOf<CreateAssignmentState>(CreateAssignmentState.Idle) }
    var title by remember { mutableStateOf("") }
    var taskId by remember { mutableStateOf<Long?>(null) }
    var maxPoints by remember { mutableStateOf("100") }

    var startDate by remember {
        mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    }
    var endDate by remember {
        mutableStateOf(
            kotlin.time.Clock.System.now()
                .plus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                .toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    var timeoutValue by remember { mutableStateOf("120") }
    var timeoutUnit by remember { mutableStateOf(TimeUnit.SECONDS) }

    var memoryValue by remember { mutableStateOf("1") }
    var memoryUnit by remember { mutableStateOf(MemoryUnit.GB) }

    var cpuValue by remember { mutableStateOf("1") }
    var cpuUnit by remember { mutableStateOf(CpuUnit.CPU) }

    var pidsLimit by remember { mutableStateOf("20") }
    var maxAttempts by remember { mutableStateOf("3") }

    var selectedGroupIds by remember { mutableStateOf<List<Long>>(emptyList()) }

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

    Column(modifier = Modifier.fillMaxWidth().heightIn(scope.screenHeight).padding(vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            NavigateBackButton(
                onNavigateBack = onNavigateBack, title = "Assignments"
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
                    cardTitle = "Create Assignment",
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
                        createState = CreateAssignmentState.Loading
                        coroutineScope.launch {
                            val result = onCreate(
                                CreateAssignmentDto(
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
                                    assignmentGroupsIds = selectedGroupIds,
                                    maximumAttempts = maxAttempts.toIntOrNull() ?: 1
                                )
                            )
                            createState = when (result) {
                                is OperationResult.Success -> CreateAssignmentState.Success
                                is OperationResult.Error -> CreateAssignmentState.Error(result.message)
                                is OperationResult.Loading -> CreateAssignmentState.Loading
                                is OperationResult.Idle -> CreateAssignmentState.Idle
                            }
                        }
                    },
                    submitButtonText = "Create",
                    isSubmitEnabled = title.isNotBlank() && taskId != null && createState is CreateAssignmentState.Idle
                )
            }
        }
    }
}


@Composable
fun DateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime) -> Unit
) {
    var year by remember { mutableStateOf(initialDateTime.year.toString()) }
    var month by remember { mutableStateOf(initialDateTime.month.number.toString()) }
    var day by remember { mutableStateOf(initialDateTime.day.toString()) }
    var hour by remember { mutableStateOf(initialDateTime.hour.toString()) }
    var minute by remember { mutableStateOf(initialDateTime.minute.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Select Date and Time", style = MaterialTheme.typography.titleLarge)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = {
                            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 4)) {
                                year = it
                            }
                        },
                        label = { Text("Year") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = {
                            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull()
                                    ?.let { v -> v in 1..12 } != false)) {
                                month = it
                            }
                        },
                        label = { Text("Month") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = day,
                        onValueChange = {
                            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull()
                                    ?.let { v -> v in 1..31 } != false)) {
                                day = it
                            }
                        },
                        label = { Text("Day") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hour,
                        onValueChange = {
                            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull()
                                    ?.let { v -> v in 0..23 } != false)) {
                                hour = it
                            }
                        },
                        label = { Text("Hour") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = minute,
                        onValueChange = {
                            if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.toIntOrNull()
                                    ?.let { v -> v in 0..59 } != false)) {
                                minute = it
                            }
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            try {
                                val newDateTime = LocalDateTime(
                                    year.toInt(),
                                    month.toInt(),
                                    day.toInt(),
                                    hour.toInt(),
                                    minute.toInt()
                                )
                                onConfirm(newDateTime)
                            } catch (e: Exception) {
                                // Invalid date
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = year.isNotEmpty() && month.isNotEmpty() && day.isNotEmpty() &&
                                hour.isNotEmpty() && minute.isNotEmpty()
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun AssignmentFormCard(
    cardTitle: String,
    title: String,
    onTitleChange: (String) -> Unit,
    taskId: Long?,
    onTaskIdChange: (Long?) -> Unit,
    taskSearchQuery: String,
    onTaskSearchQueryChange: (String) -> Unit,
    showTaskDropdown: Boolean,
    onShowTaskDropdownChange: (Boolean) -> Unit,
    availableTasks: List<Long>,
    maxPoints: String,
    onMaxPointsChange: (String) -> Unit,
    startDate: LocalDateTime,
    onStartDateClick: () -> Unit,
    endDate: LocalDateTime,
    onEndDateClick: () -> Unit,
    timeoutValue: String,
    onTimeoutValueChange: (String) -> Unit,
    timeoutUnit: TimeUnit,
    onTimeoutUnitChange: (TimeUnit) -> Unit,
    memoryValue: String,
    onMemoryValueChange: (String) -> Unit,
    memoryUnit: MemoryUnit,
    onMemoryUnitChange: (MemoryUnit) -> Unit,
    cpuValue: String,
    onCpuValueChange: (String) -> Unit,
    cpuUnit: CpuUnit,
    onCpuUnitChange: (CpuUnit) -> Unit,
    pidsLimit: String,
    onPidsLimitChange: (String) -> Unit,
    maxAttempts: String,
    onMaxAttemptsChange: (String) -> Unit,
    groupSearchQuery: String,
    onGroupSearchQueryChange: (String) -> Unit,
    showGroupDropdown: Boolean,
    onShowGroupDropdownChange: (Boolean) -> Unit,
    availableAssignmentGroups: List<AssignmentGroupDto>,
    selectedGroupIds: List<Long>,
    onSelectedGroupIdsChange: (List<Long>) -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    submitButtonText: String,
    isSubmitEnabled: Boolean,
    isReadOnly: Boolean = false,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = !isReadOnly && title.isBlank(),
                supportingText = if (!isReadOnly && title.isBlank()) {
                    { Text("Title is required") }
                } else null,
                readOnly = isReadOnly
            )

            Box {
                if (isReadOnly) {
                    OutlinedTextField(
                        value = if (taskId != null) "Task $taskId" else "",
                        onValueChange = {},
                        label = { Text("Task ID") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                } else {
                    SearchableDropdown(
                        expanded = showTaskDropdown,
                        onExpandedChange = { onShowTaskDropdownChange(it) },
                        items = availableTasks.filter { it.toString().contains(taskSearchQuery, ignoreCase = true) },
                        itemContent = { task ->
                            DropdownMenuItem(
                                text = { Text("Task $task") },
                                onClick = {
                                    onTaskIdChange(task)
                                    onTaskSearchQueryChange("")
                                    onShowTaskDropdownChange(false)
                                }
                            )
                        },
                        searchContent = {
                            // Typing happens in the anchor field; this area can show a hint or filters
                            Text("Type to filter tasks", modifier = Modifier.padding(8.dp))
                        },
                        displayContent = { anchorModifier ->
                            OutlinedTextField(
                                value = if (taskId != null) "Task $taskId" else taskSearchQuery,
                                onValueChange = { newValue ->
                                    if (taskId != null) onTaskIdChange(null)
                                    onTaskSearchQueryChange(newValue)
                                },
                                label = { Text("Task ID *") },
                                modifier = anchorModifier
                                    .fillMaxWidth()
                                    .onFocusChanged { f -> onShowTaskDropdownChange(f.isFocused) },
                                isError = taskId == null,
                                supportingText = if (taskId == null) {
                                    { Text("Please select a task") }
                                } else null,
                                trailingIcon = {
                                    Row {
                                        if (taskId != null) {
                                            IconButton(onClick = {
                                                onTaskIdChange(null)
                                                onTaskSearchQueryChange("")
                                            }) {
                                                Icon(Icons.Default.Close, "Clear selection")
                                            }
                                        }
                                        IconButton(onClick = { onShowTaskDropdownChange(!showTaskDropdown) }) {
                                            Icon(Icons.Default.ArrowDropDown, "Dropdown")
                                        }
                                    }
                                }
                            )
                        }
                    )
                }
            }

            OutlinedTextField(
                value = maxPoints,
                onValueChange = {
                    if (!isReadOnly && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                        onMaxPointsChange(it)
                    }
                },
                label = { Text("Max Points") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = isReadOnly
            )

            OutlinedTextField(
                value = "${startDate.date} ${startDate.time}",
                onValueChange = {},
                label = { Text("Start Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = if (!isReadOnly) {
                    {
                        IconButton(onClick = onStartDateClick) {
                            Icon(Icons.Default.DateRange, "Pick date")
                        }
                    }
                } else null
            )

            OutlinedTextField(
                value = "${endDate.date} ${endDate.time}",
                onValueChange = {},
                label = { Text("End Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = if (!isReadOnly) {
                    {
                        IconButton(onClick = onEndDateClick) {
                            Icon(Icons.Default.DateRange, "Pick date")
                        }
                    }
                } else null
            )

            Text(
                text = "Resource Limits",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = timeoutValue,
                    onValueChange = {
                        if (!isReadOnly && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                            onTimeoutValueChange(it)
                        }
                    },
                    label = { Text("Timeout") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = isReadOnly
                )
                if (isReadOnly) {
                    OutlinedTextField(
                        value = timeoutUnit.displayName,
                        onValueChange = {},
                        label = { Text("Unit") },
                        modifier = Modifier.weight(0.5f),
                        readOnly = true
                    )
                } else {
                    UnitSelector(
                        selectedUnit = timeoutUnit,
                        units = TimeUnit.entries,
                        onUnitSelected = onTimeoutUnitChange,
                        modifier = Modifier.weight(0.5f),
                        getDisplayName = { it.displayName }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = memoryValue,
                    onValueChange = {
                        if (!isReadOnly && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                            onMemoryValueChange(it)
                        }
                    },
                    label = { Text("Memory Limit") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = isReadOnly
                )
                if (isReadOnly) {
                    OutlinedTextField(
                        value = memoryUnit.displayName,
                        onValueChange = {},
                        label = { Text("Unit") },
                        modifier = Modifier.weight(0.5f),
                        readOnly = true
                    )
                } else {
                    UnitSelector(
                        selectedUnit = memoryUnit,
                        units = MemoryUnit.entries,
                        onUnitSelected = onMemoryUnitChange,
                        modifier = Modifier.weight(0.5f),
                        getDisplayName = { it.displayName }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = cpuValue,
                    onValueChange = {
                        if (!isReadOnly && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                            onCpuValueChange(it)
                        }
                    },
                    label = { Text("CPU Limit") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = isReadOnly
                )
                if (isReadOnly) {
                    OutlinedTextField(
                        value = cpuUnit.displayName,
                        onValueChange = {},
                        label = { Text("Unit") },
                        modifier = Modifier.weight(0.5f),
                        readOnly = true
                    )
                } else {
                    UnitSelector(
                        selectedUnit = cpuUnit,
                        units = CpuUnit.entries,
                        onUnitSelected = onCpuUnitChange,
                        modifier = Modifier.weight(0.5f),
                        getDisplayName = { it.displayName }
                    )
                }
            }

            OutlinedTextField(
                value = pidsLimit,
                onValueChange = {
                    if (!isReadOnly && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                        onPidsLimitChange(it)
                    }
                },
                label = { Text("PIDs Limit") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = isReadOnly
            )

            OutlinedTextField(
                value = maxAttempts,
                onValueChange = {
                    if (!isReadOnly && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                        onMaxAttemptsChange(it)
                    }
                },
                label = { Text("Maximum attempts") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = isReadOnly
            )

            if (!isReadOnly) {
                val groupSearchFocusRequester = remember { FocusRequester() }
                Box {
                    SearchableDropdown(
                        expanded = showGroupDropdown,
                        onExpandedChange = { onShowGroupDropdownChange(it) },
                        items = availableAssignmentGroups
                            .filter { it.id !in selectedGroupIds }
                            .filter { it.name.contains(groupSearchQuery, ignoreCase = true) },
                        itemContent = { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = {
                                    onSelectedGroupIdsChange(selectedGroupIds + (group.id!!))
                                    onGroupSearchQueryChange("")
                                    // Keep dropdown open for consecutive selections and refocus input
                                    groupSearchFocusRequester.requestFocus()
                                }
                            )
                        },
                        searchContent = {
                            Text("Type to filter groups", modifier = Modifier.padding(8.dp))
                        },
                        displayContent = { anchorModifier ->
                            OutlinedTextField(
                                value = groupSearchQuery,
                                onValueChange = { newValue ->
                                    onGroupSearchQueryChange(newValue)
                                },
                                label = { Text("Search Assignment Groups") },
                                modifier = anchorModifier
                                    .fillMaxWidth()
                                    .focusRequester(groupSearchFocusRequester)
                                    .onFocusChanged { f -> onShowGroupDropdownChange(f.isFocused) },
                                trailingIcon = {
                                    Row {
                                        if (groupSearchQuery.isNotEmpty()) {
                                            IconButton(onClick = { onGroupSearchQueryChange("") }) {
                                                Icon(Icons.Default.Close, "Clear search")
                                            }
                                        }
                                        IconButton(onClick = { onShowGroupDropdownChange(!showGroupDropdown) }) {
                                            Icon(Icons.Default.ArrowDropDown, "Dropdown")
                                        }
                                    }
                                }
                            )
                        }
                    )
                }
            }

            if (selectedGroupIds.isNotEmpty()) {
                Text("Selected Groups:", style = MaterialTheme.typography.bodyMedium)
                selectedGroupIds.forEach { groupId ->
                    val group = availableAssignmentGroups.find { it.id == groupId }
                    group?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(it.name)
                                if (!isReadOnly) {
                                    IconButton(
                                        onClick = {
                                            onSelectedGroupIdsChange(
                                                selectedGroupIds.filter { id -> id != groupId }
                                            )
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, "Remove")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isReadOnly) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.weight(1f),
                        enabled = isSubmitEnabled
                    ) {
                        Text(submitButtonText)
                    }
                } else {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

@Composable
fun <T> UnitSelector(
    selectedUnit: T,
    units: List<T>,
    onUnitSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    getDisplayName: (T) -> String
) where T : Enum<T> {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(getDisplayName(selectedUnit))
            Icon(Icons.Default.ArrowDropDown, null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(getDisplayName(unit)) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(widthDp = 1980, heightDp = 1080)
@Composable
fun CreateAssignmentComponentPreview() {
    DefaultPreview {
        CreateAssignmentComponent(
            it,
            onNavigateBack = {},
            availableAssignmentGroups = listOf(
                AssignmentGroupDto(
                    name = "Some group",
                    userIds = listOf(1),
                    assignmentIds = listOf(1),
                    id = 1
                )
            ),
            availableTasks = listOf(1),
            onCreate = { OperationResult.Loading() }
        )
    }
}