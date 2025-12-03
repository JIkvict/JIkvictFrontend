package org.jikvict.browser.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.AssignmentInfo
import org.jikvict.api.models.AssignmentResultDto
import org.jikvict.api.models.TestResult
import org.jikvict.api.models.TestSuiteResult
import org.jikvict.api.models.UserDto
import org.jikvict.browser.components.common.SearchableDropdown
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.screens.formatCpuLimit
import org.jikvict.browser.screens.formatDate
import org.jikvict.browser.screens.formatMemory
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.LocalThemeSwitcherProvider
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.round
import kotlin.random.Random

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AssignmentInfoComponent(
    scope: DefaultScreenScope,
    assignment: AssignmentDto,
    onNavigateBack: () -> Unit,
    availableUsers: List<UserDto>,
    availableGroups: List<AssignmentGroupDto>,
    infoSupplier: suspend (List<UserDto>, List<AssignmentGroupDto>) -> List<AssignmentInfo>?,
    onEditClick: (AssignmentDto) -> Unit,
    onDownloadClick: (AssignmentResultDto) -> Unit,
) = with(scope) {
    var userQuery by remember { mutableStateOf("") }
    var groupQuery by remember { mutableStateOf("") }
    var userDropdownExpanded by remember { mutableStateOf(false) }
    var groupDropdownExpanded by remember { mutableStateOf(false) }

    val userFocusRequester = remember { FocusRequester() }
    val groupFocusRequester = remember { FocusRequester() }

    var selectedUsers by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var selectedGroups by remember { mutableStateOf<List<AssignmentGroupDto>>(emptyList()) }

    var infos by remember { mutableStateOf<OperationResult<List<AssignmentInfo>>>(OperationResult.Idle()) }
    LaunchedEffect(selectedUsers, selectedGroups) {
        infos = OperationResult.Loading()
        try {
            val res = infoSupplier(selectedUsers, selectedGroups)
            infos = if (res == null) {
                OperationResult.Error("No results")
            } else {
                OperationResult.Success(res)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            infos = OperationResult.Error(e.message ?: "Unknown error")
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                NavigateBackButton(
                    onNavigateBack = onNavigateBack,
                    title = "Back"
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            if (assignment.isClosed) {
                Spacer(Modifier.width(12.dp))
                Text(
                    text = " (Closed)",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Text(
            text = "Task #${assignment.taskId}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
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
            }
            Spacer(Modifier.width(12.dp))
            Column {
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
            }

        }


        // Users selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Select users", style = MaterialTheme.typography.titleMedium)
            SearchableDropdown(
                expanded = userDropdownExpanded,
                onExpandedChange = { userDropdownExpanded = it },
                items = availableUsers.filter { u ->
                    val q = userQuery.trim().lowercase()
                    q.isEmpty() || u.userNameField.lowercase().contains(q) || u.email.lowercase().contains(q)
                },
                itemContent = { user ->
                    DropdownMenuItem(
                        text = { Text("${user.userNameField} (${user.email})") },
                        onClick = {
                            if (selectedUsers.none { it.id == user.id }) {
                                selectedUsers = selectedUsers + user
                            }
                            userQuery = ""
                            userFocusRequester.requestFocus()
                        }
                    )
                },
                searchContent = {
                    Text("Type to filter users", modifier = Modifier.padding(8.dp))
                },
                displayContent = { anchorModifier ->
                    OutlinedTextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        label = { Text("Search users") },
                        modifier = anchorModifier
                            .fillMaxWidth()
                            .focusRequester(userFocusRequester)
                            .onFocusChanged { f -> userDropdownExpanded = f.isFocused },
                        singleLine = true,
                        trailingIcon = {
                            Row {
                                if (userQuery.isNotEmpty()) {
                                    IconButton(onClick = { userQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                                IconButton(onClick = { userDropdownExpanded = !userDropdownExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                        }
                    )
                }
            )
            if (selectedUsers.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedUsers, key = { it.id }) { user ->
                        SelectableChip(
                            label = user.userNameField,
                            onRemove = { selectedUsers = selectedUsers.filterNot { it.id == user.id } }
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Select groups", style = MaterialTheme.typography.titleMedium)
            SearchableDropdown(
                expanded = groupDropdownExpanded,
                onExpandedChange = { groupDropdownExpanded = it },
                items = availableGroups.filter { g ->
                    val q = groupQuery.trim().lowercase()
                    q.isEmpty() || g.name.lowercase().contains(q)
                },
                itemContent = { group ->
                    DropdownMenuItem(
                        text = { Text(group.name) },
                        onClick = {
                            if (selectedGroups.none { it.id == group.id }) {
                                selectedGroups = selectedGroups + group
                            }
                            groupQuery = ""
                            groupFocusRequester.requestFocus()
                        }
                    )
                },
                searchContent = {
                    Text("Type to filter groups", modifier = Modifier.padding(8.dp))
                },
                displayContent = { anchorModifier ->
                    OutlinedTextField(
                        value = groupQuery,
                        onValueChange = { groupQuery = it },
                        label = { Text("Search groups") },
                        modifier = anchorModifier
                            .fillMaxWidth()
                            .onFocusChanged { f -> groupDropdownExpanded = f.isFocused },
                        singleLine = true,
                        trailingIcon = {
                            Row {
                                if (groupQuery.isNotEmpty()) {
                                    IconButton(onClick = { groupQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                                IconButton(onClick = { groupDropdownExpanded = !groupDropdownExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                            }
                        }
                    )
                }
            )
            if (selectedGroups.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedGroups, key = { it.id ?: -1 }) { group ->
                        SelectableChip(
                            label = group.name,
                            onRemove = {
                                selectedGroups = selectedGroups.filterNot { (it.id ?: -1) == (group.id ?: -1) }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        val options = listOf("Statistics", "Submissions")
        val unCheckedIcons = listOf(Icons.Outlined.QueryStats, Icons.Outlined.FilterList)
        val checkedIcons = listOf(Icons.Filled.QueryStats, Icons.Filled.FilterList)
        var selectedIndex by remember { mutableIntStateOf(0) }

        Row(
            Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            val modifiers = listOf(Modifier.weight(1f), Modifier.weight(1f))

            options.forEachIndexed { index, label ->
                ToggleButton(
                    checked = selectedIndex == index,
                    onCheckedChange = { selectedIndex = index },
                    modifier = modifiers[index].semantics { role = Role.RadioButton },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                ) {
                    Icon(
                        if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                        contentDescription = "Icon",
                    )
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(label)
                }
            }
        }

        when (val infosUnwrapped = infos) {
            is OperationResult.Success -> {
                if (selectedIndex == 0) {
                    StatsComponent(infos = infosUnwrapped.result)
                } else {
                    SubmissionComponent(infos = infosUnwrapped.result, onDownloadClick)
                }
            }

            is OperationResult.Error -> {
                Text(
                    text = "Error loading inforamtions: ${infosUnwrapped.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            is OperationResult.Loading -> {
                Box(
                    modifier = Modifier.fitContentToScreen(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator()
                }
            }

            is OperationResult.Idle -> {
                Box(
                    modifier = Modifier.fitContentToScreen(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Select users and/or groups to load statistics",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                onEditClick(assignment)
            }) {
                Text("Edit assignment", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun SelectableChip(label: String, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove")
            }
        }
    }
}

@Composable
fun SubmissionComponent(
    infos: List<AssignmentInfo>,
    download: (AssignmentResultDto) -> Unit
) {
    val isDark by LocalThemeSwitcherProvider.current.isDark
    val colors = remember(isDark) {
        object {
            val excellent = if (isDark) Color(0xFF10B981) else Color(0xFF22C55E)

            val good = if (isDark) Color(0xFFFBBF24) else Color(0xFFFACC15)

            val satisfactory = if (isDark) Color(0xFFF97316) else Color(0xFFFB923C)

            val fail = if (isDark) Color(0xFFEF4444) else Color(0xFFF87171)

            val bestBadge = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706)
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
        if (infos.isEmpty()) {
            Text(
                text = "No submissions",
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
            ).forEach { (label, color) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
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

        var expandedUsers by remember { mutableStateOf(setOf<Long>()) }
        var expandedKeys by remember { mutableStateOf(setOf<String>()) }

        infos.forEach { info ->
            val author = info.author.userNameField
            val results = info.results
            val isUserExpanded = expandedUsers.contains(info.author.id)

            val bestScore = remember(results) {
                results.maxOfOrNull { it.result?.totalEarnedPoints ?: it.points }
            }
            val bestTotal = remember(results) {
                results.firstNotNullOfOrNull { it.result?.totalPossiblePoints }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = if (isUserExpanded) 2.dp else 0.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedUsers = if (isUserExpanded) {
                                    expandedUsers - info.author.id
                                } else {
                                    expandedUsers + info.author.id
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
                                contentDescription = if (isUserExpanded) "Collapse" else "Expand",
                                modifier = Modifier.rotate(if (isUserExpanded) 0f else -90f)
                            )
                            Column {
                                Text(
                                    text = author,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (results.isNotEmpty() && bestScore != null) {
                                    Text(
                                        text = "Best: $bestScore${if (bestTotal != null) " / $bestTotal" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        val statusText =
                            if (results.isEmpty()) "No accepted submissions" else "${results.size} submissions"
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isUserExpanded && results.isNotEmpty()) {
                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            results.forEach { res ->
                                val earned = res.result?.totalEarnedPoints ?: res.points
                                val max = res.result?.totalPossiblePoints
                                val leftColor = colorFor(earned, max)
                                val key = "${info.author.id}-${res.timeStamp}-${res.points}"
                                val isResultExpanded = expandedKeys.contains(key)
                                val isBest = bestScore != null && earned == bestScore

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp),
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    expandedKeys = expandedKeys.toMutableSet().also { set ->
                                                        if (!set.add(key)) set.remove(key)
                                                    }
                                                }
                                                .heightIn(min = 56.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Colored indicator
                                            Box(
                                                modifier = Modifier
                                                    .width(6.dp)
                                                    .height(56.dp)
                                                    .background(
                                                        leftColor,
                                                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                                                    )
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                                    .fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text(
                                                            text = "${earned}${if (max != null) " / $max" else " / ?"} points",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        if (isBest) {
                                                            Surface(
                                                                color = colors.bestBadge.copy(alpha = 0.2f),
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "BEST",
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    color = colors.bestBadge,
                                                                    modifier = Modifier.padding(
                                                                        horizontal = 4.dp,
                                                                        vertical = 2.dp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Text(
                                                        text = formatDate(res.timeStamp),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = "Id: ${res.id}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        download(res)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Download,
                                                        contentDescription = "Download",
                                                        tint = MaterialTheme.colorScheme.tertiary
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

@Composable
fun StatsComponent(
    infos: List<AssignmentInfo>,
) {
    val isDark by LocalThemeSwitcherProvider.current.isDark
    println("THEME IS : $isDark")

    val chartColors = remember(isDark) {
        object {
            val excellent = if (isDark) Color(0xFF10B981) else Color(0xFF22C55E)
            val excellentSelected = if (isDark) Color(0xFF059669) else Color(0xFF16A34A)

            val good = if (isDark) Color(0xFFFBBF24) else Color(0xFFFACC15)
            val goodSelected = if (isDark) Color(0xFFF59E0B) else Color(0xFFEAB308)

            val satisfactory = if (isDark) Color(0xFFF97316) else Color(0xFFFB923C)
            val satisfactorySelected = if (isDark) Color(0xFFEA580C) else Color(0xFFF97316)

            val fail = if (isDark) Color(0xFFEF4444) else Color(0xFFF87171)
            val failSelected = if (isDark) Color(0xFFDC2626) else Color(0xFFEF4444)

            val noSubmission = if (isDark) Color(0xFF6B7280) else Color(0xFF9CA3AF)
            val noSubmissionSelected = if (isDark) Color(0xFF4B5563) else Color(0xFF6B7280)

            val attemptEven1 = if (isDark) Color(0xFF3B82F6) else Color(0xFF60A5FA)
            val attemptEven2 = if (isDark) Color(0xFF2563EB) else Color(0xFF3B82F6)

            val attemptOdd1 = if (isDark) Color(0xFFA855F7) else Color(0xFFC084FC)
            val attemptOdd2 = if (isDark) Color(0xFF9333EA) else Color(0xFFA855F7)
        }
    }

    key(isDark) {
        if (infos.isEmpty()) {
            Text(
                "For this search query no results were found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            return
        }

        // Histogram Calculation
        val histogramResult = remember(infos) {
            val buckets = IntArray(10)
            infos.forEach { info ->
                val last = info.results.lastOrNull()
                if (last != null) {
                    val earned = last.result?.totalEarnedPoints ?: last.points
                    val max = last.result?.totalPossiblePoints ?: 0
                    if (max > 0) {
                        val pct = (earned.toDouble() / max.toDouble() * 100.0).coerceIn(0.0, 100.0)
                        val bin = (pct / 10.0).toInt().coerceAtMost(9)
                        buckets[bin]++
                    }
                }
            }
            buckets
        }

        val histogramBars = remember(histogramResult, isDark) {
            histogramResult.mapIndexed { index, count ->
                val start = index * 10
                val end = start + 10
                Bars(
                    label = "${start}-${end}%",
                    values = listOf(
                        Bars.Data(
                            label = "Students",
                            value = count.toDouble(),
                            color = Brush.verticalGradient(
                                listOf(chartColors.attemptEven1, chartColors.attemptEven2)
                            )
                        )
                    )
                )
            }
        }

        val maxHistogramCount = histogramResult.maxOrNull() ?: 0
        val histogramStep = if (maxHistogramCount <= 5) 1.0 else (maxHistogramCount / 5.0)

        data class PassBucket(val label: String, val color: Color, val selectedColor: Color)

        fun isPass(info: AssignmentInfo): Boolean? {
            val last = info.results.lastOrNull() ?: return null
            val suite = last.result
            return if (suite != null) {
                val max = suite.totalPossiblePoints
                val earned = suite.totalEarnedPoints
                if (max <= 0) false else earned.toDouble() / max.toDouble() >= 0.5
            } else {
                last.points > 0
            }
        }

        val passCounts = remember(infos) {
            var pass = 0
            var fail = 0
            var none = 0
            infos.forEach { info ->
                when (isPass(info)) {
                    true -> pass++
                    false -> fail++
                    null -> none++
                }
            }
            intArrayOf(pass, fail, none)
        }

        val passBuckets = listOf(
            PassBucket(
                "Pass",
                color = chartColors.excellent,
                selectedColor = chartColors.excellentSelected
            ),
            PassBucket(
                "Fail",
                color = chartColors.fail,
                selectedColor = chartColors.failSelected
            ),
            PassBucket(
                "No submissions",
                color = chartColors.noSubmission,
                selectedColor = chartColors.noSubmissionSelected
            ),
        )

        var passData by remember(infos) {
            mutableStateOf(
                passBuckets.mapIndexed { idx, b ->
                    Pie(
                        label = b.label,
                        data = passCounts[idx].toDouble(),
                        color = b.color,
                        selectedColor = b.selectedColor,
                    )
                }
            )
        }

        val passTotal = passCounts.sum().coerceAtLeast(1)

        val attemptsCounts = remember(infos) {
            val maxObserved = infos.maxOfOrNull { it.maxAttempts } ?: 0
            IntArray(maxObserved + 1).also { arr ->
                infos.forEach { info ->
                    val idx = info.attemptsUsed.coerceIn(0, maxObserved)
                    arr[idx]++
                }
            }
        }

        val textColor = MaterialTheme.colorScheme.onBackground

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Left: Quality distribution (Histogram)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Score distribution", style = MaterialTheme.typography.titleMedium, color = textColor)
                    ColumnChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .size(260.dp),
                        data = histogramBars,
                        barProperties = BarProperties(
                            cornerRadius = Bars.Data.Radius.Rectangle(topLeft = 4.dp, topRight = 4.dp),
                            spacing = 2.dp,
                            thickness = 16.dp
                        ),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        animationMode = AnimationMode.OneByOne,
                        labelProperties = LabelProperties(
                            enabled = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor, fontSize = 10.sp),
                        ),
                        labelHelperProperties = LabelHelperProperties(
                            enabled = false
                        ),
                        indicatorProperties = HorizontalIndicatorProperties(
                            enabled = true,
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
                            count = IndicatorCount.StepBased(histogramStep),
                            position = IndicatorPosition.Horizontal.Start,
                            padding = 32.dp,
                        ),
                        gridProperties = GridProperties(
                            xAxisProperties = GridProperties.AxisProperties(
                                lineCount = 10
                            ),
                            yAxisProperties = GridProperties.AxisProperties(
                                enabled = false,
                            )
                        )
                    )
                }

                // Right: Pass/Fail distribution
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Pass / Fail / No submissions",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    PieChart(
                        modifier = Modifier.size(260.dp),
                        data = passData,
                        onPieClick = {
                            val pieIndex = passData.indexOf(it)
                            passData =
                                passData.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                        },
                        selectedScale = 1.08f,
                        scaleAnimEnterSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        colorAnimEnterSpec = tween(300),
                        colorAnimExitSpec = tween(300),
                        scaleAnimExitSpec = tween(300),
                        spaceDegreeAnimExitSpec = tween(300),
                        style = Pie.Style.Fill,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        passData.forEachIndexed { idx, pie ->
                            val count = passCounts[idx]
                            if (count == 0) return@forEachIndexed
                            val percent = (count * 100.0 / passTotal)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            if (pie.selected) pie.selectedColor else pie.color,
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = buildString {
                                        append(pie.label)
                                        append(": ")
                                        append(count)
                                        append(" (")
                                        val percentRounded = round(percent * 10.0) / 10.0
                                        append(percentRounded)
                                        append("%)")
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Attempts used", style = MaterialTheme.typography.titleMedium, color = textColor)

                    val attemptsBars = remember(infos, isDark) {
                        val labelPrefix = "Attempts"
                        attemptsCounts.mapIndexed { idx, count ->
                            val brush = if (idx % 2 == 0) {
                                Brush.verticalGradient(listOf(chartColors.attemptEven1, chartColors.attemptEven2))
                            } else {
                                Brush.verticalGradient(listOf(chartColors.attemptOdd1, chartColors.attemptOdd2))
                            }
                            Bars(
                                label = "$idx",
                                values = listOf(
                                    Bars.Data(
                                        label = labelPrefix,
                                        value = count.toDouble(),
                                        color = brush
                                    )
                                )
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Y-Axis Label (Student Count) - Rotated
                        Text(
                            text = "Student Count",
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .rotate(-90f)
                                .padding(bottom = 240.dp) // Push out
                        )

                        Column {
                            ColumnChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 24.dp, end = 8.dp) // Space for Y label
                                    .size(260.dp),
                                data = attemptsBars,
                                barProperties = BarProperties(
                                    cornerRadius = Bars.Data.Radius.Rectangle(topLeft = 6.dp, topRight = 6.dp),
                                    spacing = 3.dp,
                                    thickness = 20.dp
                                ),
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                animationMode = AnimationMode.OneByOne,
                                labelProperties = LabelProperties(
                                    enabled = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
                                ),
                                labelHelperProperties = LabelHelperProperties(
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
                                ),
                                indicatorProperties = HorizontalIndicatorProperties(
                                    enabled = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
                                    count = IndicatorCount.StepBased(1.0),
                                    position = IndicatorPosition.Horizontal.Start,
                                    padding = 32.dp,
                                ),
                                gridProperties = GridProperties(
                                    xAxisProperties = GridProperties.AxisProperties(
                                        lineCount = attemptsCounts.maxOrNull()?.plus(1) ?: 0
                                    ),
                                    yAxisProperties = GridProperties.AxisProperties(
                                        enabled = false,
                                    )
                                )
                            )
                            // X-Axis Label
                            Text(
                                text = "Number of Attempts",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Right: empty placeholder to keep 2-per-row layout
                Column(modifier = Modifier.weight(1f)) {}
            }
        }
    }
}

@Composable
@Preview(widthDp = 1920, heightDp = 1080)
fun AssignmentInfoComponentPreview() {
    DefaultPreview {
        AssignmentInfoComponent(
            it,
            assignment = AssignmentDto(
                id = 0,
                title = "Some title",
                taskId = 0,
                maxPoints = 0,
                startDate = "",
                endDate = "",
                timeOutSeconds = 0,
                memoryLimit = 0,
                cpuLimit = 0,
                pidsLimit = 0,
                isClosed = true,
                maximumAttempts = 0,
                assignmentGroupsIds = listOf(1, 2, 3),
                description = ""
            ),
            onNavigateBack = { },
            availableUsers = listOf(
                UserDto(1, "Alice", "alice@example.com", "A001", roles = setOf("USER"), assignmentGroups = emptySet()),
                UserDto(2, "Bob", "bob@example.com", "A002", roles = setOf("ADMIN"), assignmentGroups = emptySet()),
                UserDto(
                    3,
                    "Charlie",
                    "charlie@example.com",
                    "A003",
                    roles = setOf("USER"),
                    assignmentGroups = emptySet()
                ),
            ),
            availableGroups = listOf(
                AssignmentGroupDto(name = "Group 1", userIds = listOf(1, 2), assignmentIds = listOf(), id = 1),
                AssignmentGroupDto(name = "Group 2", userIds = listOf(2, 3), assignmentIds = listOf(), id = 2),
                AssignmentGroupDto(name = "Group 3", userIds = listOf(1, 3), assignmentIds = listOf(), id = 3),
            ),
            infoSupplier = { _, _ ->
                // Mock diverse data for charts preview
                val now = "2025-01-01T00:00:00Z"
                fun suite(earned: Int, max: Int) = TestSuiteResult(
                    testResults = emptyList<TestResult>(),
                    totalPossiblePoints = max,
                    totalEarnedPoints = earned
                )

                fun result(earned: Int, max: Int) = AssignmentResultDto(
                    id = Random.nextInt().toLong(),
                    timeStamp = now,
                    points = earned,
                    result = suite(earned, max)
                )
                listOf(
                    // No submissions
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 1,
                        maxAttempts = 3,
                        attemptsUsed = 0,
                        results = emptyList(),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    // Excellent
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 2,
                        maxAttempts = 3,
                        attemptsUsed = 1,
                        results = listOf(result(95, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 3,
                        maxAttempts = 3,
                        attemptsUsed = 2,
                        results = listOf(result(90, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    // Good
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 4,
                        maxAttempts = 3,
                        attemptsUsed = 2,
                        results = listOf(result(80, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 5,
                        maxAttempts = 3,
                        attemptsUsed = 1,
                        results = listOf(result(70, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    // Satisfactory
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 6,
                        maxAttempts = 3,
                        attemptsUsed = 3,
                        results = listOf(result(60, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 7,
                        maxAttempts = 3,
                        attemptsUsed = 0,
                        results = listOf(result(55, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    // Fail
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 8,
                        maxAttempts = 3,
                        attemptsUsed = 3,
                        results = listOf(result(30, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 9,
                        maxAttempts = 3,
                        attemptsUsed = 2,
                        results = listOf(result(10, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    // Fallback by points without suite
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 10,
                        maxAttempts = 5,
                        attemptsUsed = 4,
                        results = listOf(
                            AssignmentResultDto(
                                timeStamp = now,
                                points = 1,
                                result = null,
                                id = 1
                            )
                        ),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 11,
                        maxAttempts = 5,
                        attemptsUsed = 5,
                        results = listOf(
                            AssignmentResultDto(
                                timeStamp = now,
                                points = 0,
                                result = null,
                                id = 1
                            )
                        ),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                    // Various attempts used values
                    AssignmentInfo(
                        assignmentId = 0,
                        taskId = 12,
                        maxAttempts = 5,
                        attemptsUsed = 5,
                        results = listOf(result(85, 100)),
                        unacceptedSubmissions = emptyList(),
                        author = UserDto(
                            0, "No Submissions User", "123",
                            aisId = "123",
                            roles = setOf(),
                            assignmentGroups = setOf()
                        )
                    ),
                )
            },
            {},
            {}
        )
    }
}