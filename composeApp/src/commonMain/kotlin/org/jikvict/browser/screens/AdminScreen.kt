package org.jikvict.browser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.api.models.AssignmentResultAdminDto
import org.jikvict.api.models.SubmissionDto
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.viewmodel.AdminScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

@Composable
private fun AdminScreenComposable(defaultScope: DefaultScreenScope) = with(defaultScope) {
    val vm = koinViewModel<AdminScreenViewModel>()

    LaunchedEffect(Unit) { vm.loadGroups() }

    val groups by vm.groups.collectAsState()
    val selectedGroupId by vm.selectedGroupId.collectAsState()
    val overview by vm.overview.collectAsState()
    val stats by vm.stats.collectAsState()

    var groupQuery by remember { mutableStateOf("") }
    var userQuery by remember { mutableStateOf("") }

    LaunchedEffect(groups, selectedGroupId, overview) {
        vm.recomputeStats()
    }

    // Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Admin Panel",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Manage groups, students, and assignments",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    HorizontalDivider()

    // Groups & Students Selection
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Groups section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Select Group",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedTextField(
                value = groupQuery,
                onValueChange = { groupQuery = it },
                label = { Text("Search groups by name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            val filteredGroups = groups.filter { it.name.contains(groupQuery, ignoreCase = true) }
            if (filteredGroups.isEmpty()) {
                Text(
                    "No groups found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredGroups) { g ->
                        val isSelected = g.id == selectedGroupId
                        FilledTonalButton(
                            onClick = { vm.selectGroup(g.id ?: -1) },
                            colors = if (isSelected) {
                                ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                ButtonDefaults.filledTonalButtonColors()
                            }
                        ) {
                            Text(g.name)
                        }
                    }
                }
            }

            HorizontalDivider()

            // Students section
            val users = groups.firstOrNull { it.id == selectedGroupId }?.userIds ?: emptyList()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Select Student (${users.size} total)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedTextField(
                value = userQuery,
                onValueChange = { userQuery = it },
                label = { Text("Search students by ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = users.isNotEmpty()
            )

            val filteredUsers = users.filter { it.toString().contains(userQuery, ignoreCase = true) }
            if (selectedGroupId <= 0) {
                Text(
                    "Please select a group first",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (filteredUsers.isEmpty()) {
                Text(
                    "No students match the query",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredUsers) { uid ->
                        FilledTonalButton(onClick = { vm.selectUser(uid) }) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("ID: $uid")
                        }
                    }
                }
            }
        }
    }

    // Student Overview
    overview?.let { ov ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Student header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            ov.userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Student ID: ${ov.userId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                // Statistics Cards
                Text(
                    "Statistics Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total",
                        value = stats.submissionsTotal.toString(),
                        icon = Icons.Default.Pending,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Done",
                        value = stats.submissionsDone.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Failed",
                        value = stats.submissionsFailed.toString(),
                        icon = Icons.Default.Error,
                        color = Color(0xFFF44336),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Rejected",
                        value = stats.submissionsRejected.toString(),
                        icon = Icons.Default.Error,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Results",
                        value = stats.resultsCount.toString(),
                        icon = Icons.Default.Star,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Points",
                        value = stats.totalPoints.toString(),
                        icon = Icons.Default.Star,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending",
                        value = stats.submissionsPending.toString(),
                        icon = Icons.Default.Pending,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.weight(1f))
                }

                HorizontalDivider()

                // Submissions Section
                OutlinedContentContainer(label = "Submissions History") {
                    if (ov.submissions.isEmpty()) {
                        Text(
                            "No submissions yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        SubmissionsList(ov.submissions, onDelete = vm::deleteSubmission)
                    }
                }

                // Results Section
                OutlinedContentContainer(label = "Assignment Results (Edit Points)") {
                    if (ov.results.isEmpty()) {
                        Text(
                            "No results yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        ResultsList(ov.results, onUpdate = { id, points -> vm.updatePoints(id, points) })
                    }
                }

                // Assignments Summary
                val assignmentsSummary = remember(ov.results) {
                    ov.results.groupBy { it.assignmentId }.map { (aId, items) ->
                        aId to items.sumOf { it.points }
                    }.sortedByDescending { it.second }
                }
                if (assignmentsSummary.isNotEmpty()) {
                    OutlinedContentContainer(label = "Assignments Summary") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            assignmentsSummary.forEach { (aId, totalPoints) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Assignment #$aId",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFFFD700),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                "$totalPoints pts",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
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
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubmissionsList(submissions: List<SubmissionDto>, onDelete: (Long) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        submissions.forEach { s ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (s.status.name) {
                        "DONE" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        "FAILED" -> Color(0xFFF44336).copy(alpha = 0.1f)
                        "REJECTED" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        "PENDING" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                when (s.status.name) {
                                    "DONE" -> Icons.Default.CheckCircle
                                    "FAILED", "REJECTED" -> Icons.Default.Error
                                    else -> Icons.Default.Pending
                                },
                                contentDescription = null,
                                tint = when (s.status.name) {
                                    "DONE" -> Color(0xFF4CAF50)
                                    "FAILED" -> Color(0xFFF44336)
                                    "REJECTED" -> Color(0xFFFF9800)
                                    else -> Color(0xFF2196F3)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Submission #${s.id}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            "Status: ${s.status}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Created: ${s.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        s.assignmentId?.let {
                            Text(
                                "Assignment: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        s.message?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { onDelete(s.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultsList(results: List<AssignmentResultAdminDto>, onUpdate: (Long, Int) -> Unit) {
    val edited = remember { mutableStateMapOf<Long, Int>() }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        results.sortedByDescending { it.timeStamp }.forEach { r ->
            var points by remember(r.id) { mutableIntStateOf(r.points) }
            val hasChanges = edited.containsKey(r.id) && edited[r.id] != r.points

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasChanges) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (hasChanges) 4.dp else 1.dp
                )
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Result #${r.id}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Assignment: ${r.assignmentId}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Timestamp: ${r.timeStamp}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = points.toString(),
                            onValueChange = { newVal ->
                                val newPoints = newVal.toIntOrNull()
                                if (newPoints != null && newPoints >= 0) {
                                    points = newPoints
                                    edited[r.id] = points
                                }
                            },
                            label = { Text("Points") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )

                        Button(
                            onClick = {
                                onUpdate(r.id, edited[r.id] ?: points)
                                edited.remove(r.id)
                            },
                            enabled = hasChanges,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Save")
                        }
                    }

                    if (hasChanges) {
                        Text(
                            "⚠ Unsaved changes: ${r.points} → ${edited[r.id]}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Serializable
@SerialName("admin")
class AdminScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { AdminScreenComposable(it) }
}

object AdminScreenRouterRegistrar : ScreenRouterRegistrar<AdminScreen> {
    override val screen: KClass<AdminScreen>
        get() = AdminScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen = AdminScreen()
}

object AdminScreenRegistrar : ScreenRegistrar<AdminScreen> by createRegistrar()