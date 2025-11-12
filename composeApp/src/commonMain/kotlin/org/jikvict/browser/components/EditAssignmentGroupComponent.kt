package org.jikvict.browser.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.responsive

sealed class EditGroupState {
    object Idle : EditGroupState()
    object Loading : EditGroupState()
    data class Success(val group: AssignmentGroupDto) : EditGroupState()
    data class Error(val message: String) : EditGroupState()
}

@Composable
fun InfoAssignmentGroupComponent(
    scope: DefaultScreenScope,
    onNavigateBack: () -> Unit = {},
    group: AssignmentGroupDto,
    allUsers: List<User> = emptyList(),
    assignments: List<AssignmentDto> = emptyList(),
    onAssignmentClick: (AssignmentDto) -> Unit = {},
    onUpdate: suspend (AssignmentGroupDto) -> OperationResult<AssignmentGroupDto>,
    onNavigateToUpdated: (AssignmentGroupDto) -> Unit = {},
) = with(scope) {
    var groupName by remember { mutableStateOf(group.name) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUsers by remember {
        mutableStateOf(allUsers.filter { it.id in group.userIds })
    }
    var editState by remember { mutableStateOf<EditGroupState>(EditGroupState.Idle) }

    val coroutineScope = rememberCoroutineScope()

    val filteredUsers = allUsers.filter { user ->
        user.name.contains(searchQuery, ignoreCase = true) &&
                !selectedUsers.any { it.id == user.id }
    }

    fun resetToCurrent() {
        groupName = group.name
        searchQuery = ""
        selectedUsers = allUsers.filter { it.id in group.userIds }
    }

    if (editState !is EditGroupState.Idle) {
        Dialog(
            onDismissRequest = {
                if (editState !is EditGroupState.Loading) {
                    if (editState is EditGroupState.Success) {
                    }
                    editState = EditGroupState.Idle
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = editState !is EditGroupState.Loading,
                dismissOnClickOutside = editState !is EditGroupState.Loading
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
                    when (editState) {
                        is EditGroupState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Updating assignment group...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        is EditGroupState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "Assignment group updated successfully!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        editState = EditGroupState.Idle
                                        resetToCurrent()
                                        onNavigateBack()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Back")
                                }
                                Button(
                                    onClick = {
                                        onNavigateToUpdated((editState as EditGroupState.Success).group)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Go to Group")
                                }
                            }
                        }

                        is EditGroupState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFF44336)
                            )
                            Text(
                                text = "Error updating group",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = (editState as EditGroupState.Error).message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    editState = EditGroupState.Idle
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Try Again")
                            }
                        }

                        else -> { /* Idle handled outside */
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fitContentToScreen(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                NavigateBackButton(
                    onNavigateBack = onNavigateBack,
                    title = "Assignment Groups"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.responsive(mainColumnModifier).verticalScroll(scope.verticalScroll),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Edit Assignment Group",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            OutlinedTextField(
                                value = groupName,
                                onValueChange = { groupName = it },
                                label = { Text("Group name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (assignments.isNotEmpty()) {
                                Text(
                                    text = "Assignments:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(280.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 16.dp),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        .heightIn(max = scope.screenHeight),
                                ) {
                                    items(assignments.filter { it.id in group.assignmentIds }) {
                                        AssignmentCard(
                                            assignment = it,
                                            onClick = { onAssignmentClick(it) },
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No assignments in this group",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Search students") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "Search")
                                }
                            )

                            if (searchQuery.isNotEmpty() && filteredUsers.isNotEmpty()) {
                                Text(
                                    text = "Search results:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                LazyColumn(
                                    modifier = Modifier.height(120.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(filteredUsers) { user ->
                                        OutlinedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CardDefaults.outlinedShape)
                                                .clickable {
                                                    selectedUsers = selectedUsers + user
                                                    searchQuery = ""
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = user.name,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = user.email,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Add",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (selectedUsers.isNotEmpty()) {
                                Text(
                                    text = "Students (${selectedUsers.size}):",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                LazyColumn(
                                    modifier = Modifier.height(200.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(selectedUsers) { user ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = user.name,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = user.email,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        selectedUsers = selectedUsers.filter { it.id != user.id }
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Remove",
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
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
                                Button(
                                    onClick = onNavigateBack,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        editState = EditGroupState.Loading
                                        coroutineScope.launch {
                                            val dto = AssignmentGroupDto(
                                                id = group.id,
                                                name = groupName,
                                                userIds = selectedUsers.map { it.id },
                                                assignmentIds = group.assignmentIds
                                            )
                                            val result = onUpdate(dto)
                                            editState = when (result) {
                                                is OperationResult.Success -> EditGroupState.Success(result.result)
                                                is OperationResult.Error -> EditGroupState.Error(result.message)
                                                else -> EditGroupState.Idle
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = groupName.isNotEmpty() && selectedUsers.isNotEmpty() && editState is EditGroupState.Idle
                                ) {
                                    Text("Save")
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
@Preview
fun EditAssignmentGroupComponentPreview() {
    DefaultPreview {
        InfoAssignmentGroupComponent(
            scope = it,
            onNavigateBack = {},
            group = AssignmentGroupDto(
                id = 1,
                name = "Group A",
                userIds = listOf(1, 2),
                assignmentIds = listOf(10, 11)
            ),
            allUsers = listOf(
                User(1, "Alice", "alice@example.com"),
                User(2, "Bob", "bob@example.com"),
                User(3, "Carol", "carol@example.com")
            ),
            onUpdate = { OperationResult.Success(it) },
            onNavigateToUpdated = {}
        )
    }
}
