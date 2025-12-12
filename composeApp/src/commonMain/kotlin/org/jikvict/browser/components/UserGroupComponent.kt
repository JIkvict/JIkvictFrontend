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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.util.DefaultPreview

@Composable
fun UserGroupComponent(
    assignmentGroups: List<AssignmentGroupDto>,
    onNavigateBack: () -> Unit = {},
    onGroupClick: (AssignmentGroupDto) -> Unit = {},
    scope: DefaultScreenScope,
    onAddGroupClick: () -> Unit = {},
) =
    with(scope) {
        var groupSearch by remember { mutableStateOf("") }

        val filteredGroups =
            remember(assignmentGroups, groupSearch) {
                if (groupSearch.isBlank()) {
                    assignmentGroups
                } else {
                    assignmentGroups.filter {
                        it.name.contains(groupSearch, ignoreCase = true)
                    }
                }
            }

        Box(modifier = Modifier.fitContentToScreen(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                NavigateBackButton(onNavigateBack = onNavigateBack, title = "Admin panel")

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(16.dp)
                            .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedTextField(
                        value = groupSearch,
                        onValueChange = { groupSearch = it },
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
                        modifier =
                            Modifier.size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(12.dp)
                                ),
                        onClick = onAddGroupClick
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (filteredGroups.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =
                                if (groupSearch.isBlank()) "No groups available"
                                else "Groups not found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(250.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier =
                            Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                .weight(1f),
                    ) {
                        items(filteredGroups) {
                            GroupCard(group = it, onClick = { onGroupClick(it) })
                        }
                    }
                }
            }
        }
    }

@Composable
private fun GroupCard(
    group: AssignmentGroupDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${group.userIds.size} students",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            if (group.assignmentIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${group.assignmentIds.size} tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
@Preview
@Preview(widthDp = 1920, heightDp = 1080)
@Preview(widthDp = 1080, heightDp = 1920)
@Preview(widthDp = 720, heightDp = 1280)
fun UserGroupComponentPreview() {
    DefaultPreview {
        UserGroupComponent(
            assignmentGroups = generateRandomGroups(15),
            onNavigateBack = {},
            onGroupClick = {},
            it
        )
    }
}

fun generateRandomGroups(num: Int): List<AssignmentGroupDto> {
    return (1..num).map { index ->
        AssignmentGroupDto(
            name = "Group $index",
            id = index.toLong(),
            userIds = (1L..(3L..15L).random()).toList(),
            assignmentIds = (1L..(3L..15L).random()).toList()
        )
    }
}
