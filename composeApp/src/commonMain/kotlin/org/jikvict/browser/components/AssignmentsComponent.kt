package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.util.DefaultPreview
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jikvict.api.models.AssignmentDto
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun AssignmentsComponent(
    assignments: List<AssignmentDto>,
    onNavigateBack: () -> Unit = {},
    onAssignmentClick: (AssignmentDto) -> Unit = {},
    scope: DefaultScreenScope,
    onAddAssignmentClick: () -> Unit = {},
) = with(scope) {
    var assignmentSearch by remember { mutableStateOf("") }

    val filteredAssignments = remember(assignments, assignmentSearch) {
        if (assignmentSearch.isBlank()) {
            assignments
        } else {
            assignments.filter {
                it.title.contains(assignmentSearch, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier.fitContentToScreen(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            NavigateBackButton(
                onNavigateBack = onNavigateBack,
                title = "Admin panel"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = assignmentSearch,
                    onValueChange = { assignmentSearch = it },
                    label = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                IconComponentUnsized(
                    iconSize = 24.dp,
                    iconVector = Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                    onClick = onAddAssignmentClick
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (filteredAssignments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (assignmentSearch.isBlank()) "No assignments available" else "Assignments not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).heightIn(max = scope.screenHeight),
                ) {
                    items(filteredAssignments) {
                        AssignmentCard(
                            assignment = it,
                            onClick = { onAssignmentClick(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignmentCard(
    assignment: AssignmentDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(280.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Max: ${assignment.maxPoints} pts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Task ID: ${assignment.taskId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Attempts: ${assignment.maximumAttempts}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
@Preview
@Preview(widthDp = 1920, heightDp = 1080)
@Preview(widthDp = 1080, heightDp = 1920)
@Preview(widthDp = 720, heightDp = 1280)
fun AssignmentsComponentPreview() {
    DefaultPreview(false) {
        AssignmentsComponent(
            assignments = generateRandomAssignments(15),
            onNavigateBack = {},
            onAssignmentClick = {},
            it
        )
    }
}

@OptIn(ExperimentalTime::class)
fun generateRandomAssignments(num: Int): List<AssignmentDto> {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val titles = listOf("Alfred Singh", "Sara Cui", "Sharon Chandra", "Francisca Abbas", "Igor Sanchez", "Xiaodong Yu", "Pablo Ullah", "Ha Jimenez", "Mariya Hasan", "Asma Prakash")
    return (1..num).map { _ ->
        AssignmentDto(
            id = 9175,
            title = titles.random(),
            taskId = 9848,
            maxPoints = 2405,
            startDate = now.toString(),
            endDate = now.toString(),
            timeOutSeconds = 6018,
            memoryLimit = 8630,
            cpuLimit = 5570,
            pidsLimit = 7445,
            isClosed = false,
            maximumAttempts = 1123,
            description = "nam",
            assignmentGroupsIds = listOf()
        )
    }
}