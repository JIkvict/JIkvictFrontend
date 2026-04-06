package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.AssignmentInfoAdmin
import org.jikvict.api.models.AssignmentResultAdminDto
import org.jikvict.api.models.UserDto
import org.jikvict.browser.components.common.SearchableDropdown
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.screens.formatDate
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.LocalThemeSwitcherProvider

data class StudentAssignmentInfo(val assignmentTitle: String, val info: AssignmentInfoAdmin)
data class StudentStats(val assignments: List<StudentAssignmentInfo>)

@Composable
fun StudentsComponent(
    availableUsers: List<UserDto>,
    statsProvider: suspend (UserDto) -> StudentStats,
    onDownloadClick: (AssignmentResultAdminDto) -> Unit,
    onGroupClick: (AssignmentGroupDto) -> Unit,
    scope: DefaultScreenScope,
    onNavigateBack: () -> Unit = {},
    initialUserName: String? = null,
    onUserSelected: (UserDto) -> Unit = {}
) {
    val selectedUser = remember(initialUserName, availableUsers) {
        availableUsers.find { it.userNameField == initialUserName }
    }

    var userQuery by remember(initialUserName) {
        mutableStateOf(initialUserName ?: "")
    }

    var userDropdownExpanded by remember { mutableStateOf(false) }
    val userFocusRequester = remember { FocusRequester() }

    var statsResult by remember {
        mutableStateOf<OperationResult<StudentStats>>(OperationResult.Idle())
    }

    LaunchedEffect(selectedUser) {
        if (selectedUser != null) {
            statsResult = OperationResult.Loading()
            try {
                val stats = statsProvider(selectedUser)
                statsResult = OperationResult.Success(stats)
            } catch (e: Exception) {
                statsResult = OperationResult.Error(e.message ?: "Unknown error")
            }
        } else {
            statsResult = OperationResult.Idle()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = scope.screenHeight)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            NavigateBackButton(
                onNavigateBack = onNavigateBack,
                title = "Back",
                padding = PaddingValues(0.dp)
            )
        }

        Text("Select student", style = MaterialTheme.typography.titleMedium)

        SearchableDropdown(
            expanded = userDropdownExpanded,
            onExpandedChange = { userDropdownExpanded = it },
            items = availableUsers.filter { u ->
                val q = userQuery.trim().lowercase()
                q.isEmpty() ||
                        u.userNameField.lowercase().contains(q) ||
                        u.email.lowercase().contains(q)
            },
            itemContent = { user ->
                DropdownMenuItem(
                    text = { Text("${user.userNameField} (${user.email})") },
                    onClick = {
                        userDropdownExpanded = false
                        onUserSelected(user)
                    }
                )
            },
            searchContent = { Text("Type to filter users", modifier = Modifier.padding(8.dp)) },
            displayContent = { anchorModifier ->
                OutlinedTextField(
                    value = userQuery,
                    onValueChange = {
                        userQuery = it
                        if (!userDropdownExpanded) userDropdownExpanded = true
                    },
                    label = { Text("Search user") },
                    modifier = anchorModifier
                        .fillMaxWidth()
                        .focusRequester(userFocusRequester)
                        .onFocusChanged { f ->
                            if (f.isFocused) userDropdownExpanded = true
                        },
                    singleLine = true,
                    trailingIcon = {
                        Row {
                            if (userQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        userQuery = ""
                                        userDropdownExpanded = true
                                        userFocusRequester.requestFocus()
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                            IconButton(
                                onClick = {
                                    userDropdownExpanded = !userDropdownExpanded
                                }
                            ) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        }
                    }
                )
            }
        )

        if (selectedUser == null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "First you should select student",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            when (val res = statsResult) {
                is OperationResult.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is OperationResult.Error -> {
                    Text(
                        "Error loading stats: ${res.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is OperationResult.Success -> {
                    StudentOverview(user = selectedUser, onGroupClick)
                    Spacer(modifier = Modifier.height(16.dp))
                    StudentAssignmentsList(
                        assignments = res.result.assignments,
                        onDownloadClick = onDownloadClick
                    )
                }

                is OperationResult.Idle -> {}
            }
        }
    }
}


@Composable
fun StudentOverview(
    user: UserDto,
    onGroupClick: (AssignmentGroupDto) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(user.userNameField, style = MaterialTheme.typography.headlineSmall)
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Assignment Groups", style = MaterialTheme.typography.titleSmall)
                if (user.assignmentGroups.isEmpty()) {
                    Text("None", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        user.assignmentGroups.forEach { group ->
                            SuggestionChip(
                                onClick = { onGroupClick(group) },
                                label = { Text(group.name) },
                                colors = SuggestionChipDefaults.suggestionChipColors().copy(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    labelColor = MaterialTheme.colorScheme.onSurface

                                )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        }
    }
}

@Composable
fun StudentAssignmentsList(
    assignments: List<StudentAssignmentInfo>,
    onDownloadClick: (AssignmentResultAdminDto) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    val isDark by LocalThemeSwitcherProvider.current.isDark
    val colors =
        remember(isDark) {
            object {
                val excellent = if (isDark) Color(0xFF10B981) else Color(0xFF22C55E)
                val good = if (isDark) Color(0xFFFBBF24) else Color(0xFFFACC15)
                val satisfactory = if (isDark) Color(0xFFF97316) else Color(0xFFFB923C)
                val fail = if (isDark) Color(0xFFEF4444) else Color(0xFFF87171)
                val bestBadge = primary
            }
        }

    fun colorFor(earned: Int, max: Int?): Color {
        if (max == null || max <= 0) return colors.fail
        val pct = earned.toFloat() / max.toFloat() * 100f
        return when {
            pct >= 90f -> colors.excellent
            pct >= 70f -> colors.good
            pct >= 50f -> colors.satisfactory
            else -> colors.fail
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (assignments.isEmpty()) {
            Text(
                text = "No assignments found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                "Excellent (>90%)" to colors.excellent,
                "Good (70-89%)" to colors.good,
                "Satisfactory (50-69%)" to colors.satisfactory,
                "Fail (<50%)" to colors.fail
            )
                .forEach { (label, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier =
                                Modifier.size(12.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
        }

        var expandedAssignments by remember { mutableStateOf(setOf<Long>()) }
        var expandedKeys by remember { mutableStateOf(setOf<String>()) }

        assignments.forEach { item ->
            val info = item.info
            val title = item.assignmentTitle
            val results = info.results
            val isAssignmentExpanded = expandedAssignments.contains(info.assignmentId)

            val bestScore =
                remember(results) {
                    results.maxOfOrNull { it.result?.totalEarnedPoints ?: it.points }
                }
            val bestTotal =
                remember(results) {
                    results.firstNotNullOfOrNull { it.result?.totalPossiblePoints }
                }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = if (isAssignmentExpanded) 2.dp else 0.dp
            ) {
                Column {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clickable {
                                    expandedAssignments =
                                        if (isAssignmentExpanded) {
                                            expandedAssignments - info.assignmentId
                                        } else {
                                            expandedAssignments + info.assignmentId
                                        }
                                }
                                .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription =
                                    if (isAssignmentExpanded) "Collapse" else "Expand",
                                modifier =
                                    Modifier.rotate(if (isAssignmentExpanded) 0f else -90f)
                            )
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (results.isNotEmpty() && bestScore != null) {
                                    Text(
                                        text =
                                            "Best: $bestScore${if (bestTotal != null) " / $bestTotal" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        val statusText =
                            if (results.isEmpty()) "No accepted submissions"
                            else "${results.size} submissions"
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isAssignmentExpanded && results.isNotEmpty()) {
                        Column(
                            modifier =
                                Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            results.forEach { res ->
                                val earned = res.result?.totalEarnedPoints ?: res.points
                                val max = res.result?.totalPossiblePoints
                                val leftColor = colorFor(earned, max)
                                val key = "${info.assignmentId}-${res.timeStamp}-${res.points}"
                                val isResultExpanded = expandedKeys.contains(key)
                                val isBest = bestScore != null && earned == bestScore

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp),
                                ) {
                                    Column {
                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth()
                                                    .clickable {
                                                        expandedKeys =
                                                            expandedKeys
                                                                .toMutableSet()
                                                                .also { set ->
                                                                    if (!set.add(
                                                                            key
                                                                        )
                                                                    )
                                                                        set.remove(
                                                                            key
                                                                        )
                                                                }
                                                    }
                                                    .heightIn(min = 56.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier =
                                                    Modifier.width(6.dp)
                                                        .height(56.dp)
                                                        .background(
                                                            leftColor,
                                                            RoundedCornerShape(
                                                                topStart =
                                                                    12.dp,
                                                                bottomStart =
                                                                    12.dp
                                                            )
                                                        )
                                            )
                                            Row(
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    )
                                                        .fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment =
                                                            Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                            Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text(
                                                            text =
                                                                "${earned}${if (max != null) " / $max" else " / ?"} points",
                                                            style =
                                                                MaterialTheme.typography
                                                                    .bodyMedium,
                                                            color =
                                                                MaterialTheme.colorScheme
                                                                    .onSurface
                                                        )
                                                        if (isBest) {
                                                            Surface(
                                                                color =
                                                                    colors.bestBadge.copy(
                                                                        alpha = 0.2f
                                                                    ),
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "BEST",
                                                                    style =
                                                                        MaterialTheme
                                                                            .typography
                                                                            .labelSmall,
                                                                    color = colors.bestBadge,
                                                                    modifier =
                                                                        Modifier.padding(
                                                                            horizontal =
                                                                                4.dp,
                                                                            vertical =
                                                                                2.dp
                                                                        )
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Text(
                                                        text = formatDate(res.timeStamp),
                                                        style =
                                                            MaterialTheme.typography
                                                                .bodySmall,
                                                        color =
                                                            MaterialTheme.colorScheme
                                                                .onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = "Id: ${res.id}",
                                                        style =
                                                            MaterialTheme.typography
                                                                .bodySmall,
                                                        color =
                                                            MaterialTheme.colorScheme
                                                                .onSurfaceVariant
                                                    )
                                                }

                                                IconButton(
                                                    onClick = { onDownloadClick(res) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Download,
                                                        contentDescription = "Download",
                                                        tint =
                                                            MaterialTheme.colorScheme
                                                                .tertiary
                                                    )
                                                }
                                            }
                                        }
                                        if (isResultExpanded) {
                                            SubmissionResultComponent(assignmentResultDto = res)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 1980, heightDp = 1280)
@Composable
fun StudentsComponentPreview() {
    DefaultPreview {
        StudentsComponent(
            initialUserName = "John Doe",
            availableUsers =
                listOf(
                    UserDto(
                        id = 1,
                        userNameField = "John Doe",
                        email = "john@example.com",
                        aisId = "123",
                        roles = setOf(),
                        assignmentGroups = setOf(
                            AssignmentGroupDto(
                                "Group A",
                                userIds = listOf(1),
                                assignmentIds = listOf(1)
                            )
                        )
                    ),
                ),
            statsProvider = {user ->
                StudentStats(
                    assignments =
                        List(15) {
                            StudentAssignmentInfo(
                                assignmentTitle = "Intro to Kotlin",
                                info =
                                    AssignmentInfoAdmin(
                                        assignmentId = 1,
                                        taskId = 1,
                                        maxAttempts = 3,
                                        attemptsUsed = 1,
                                        results = listOf(),
                                        unacceptedSubmissions =
                                            listOf(),
                                        author = user,
                                    )
                            )
                        }
                )
            },
            onDownloadClick = {},
            scope = it,
            onGroupClick = {}
        )
    }
}
