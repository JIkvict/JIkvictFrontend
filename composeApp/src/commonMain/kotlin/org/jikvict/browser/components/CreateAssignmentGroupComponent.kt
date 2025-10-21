package org.jikvict.browser.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.models.AssignmentGroup
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.responsive

data class User(
    val id: Long,
    val name: String,
    val email: String
)

sealed class CreateGroupState {
    object Idle : CreateGroupState()
    object Loading : CreateGroupState()
    data class Success(val group: AssignmentGroupDto) : CreateGroupState()
    data class Error(val message: String) : CreateGroupState()
}

@Composable
fun CreateAssignmentGroupComponent(
    onNavigateBack: () -> Unit = {},
    onCreate: suspend (AssignmentGroupDto) -> OperationResult<AssignmentGroupDto>,
    allUsers: List<User> = emptyList(),
    scope: DefaultScreenScope,
    onNavigateToCreated: (AssignmentGroupDto) -> Unit = {},
) = with(scope) {
    var groupName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUsers by remember { mutableStateOf(listOf<User>()) }
    var createState by remember { mutableStateOf<CreateGroupState>(CreateGroupState.Idle) }

    val coroutineScope = rememberCoroutineScope()

    val filteredUsers = allUsers.filter { user ->
        user.name.contains(searchQuery, ignoreCase = true) &&
                !selectedUsers.any { it.id == user.id }
    }

    fun clearForm() {
        groupName = ""
        searchQuery = ""
        selectedUsers = emptyList()
    }

    if (createState !is CreateGroupState.Idle) {
        Dialog(
            onDismissRequest = {
                if (createState !is CreateGroupState.Loading) {
                    createState = CreateGroupState.Idle
                    if (createState is CreateGroupState.Success) {
                        clearForm()
                    }
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = createState !is CreateGroupState.Loading,
                dismissOnClickOutside = createState !is CreateGroupState.Loading
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
                    when (createState) {
                        is CreateGroupState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Creating assignment group...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        is CreateGroupState.Success -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "Assignment group created successfully!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        createState = CreateGroupState.Idle
                                        clearForm()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Create Another")
                                }
                                Button(
                                    onClick = {
                                        onNavigateToCreated((createState as CreateGroupState.Success).group)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Go to Group")
                                }
                            }
                        }

                        is CreateGroupState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFF44336)
                            )
                            Text(
                                text = "Error creating group",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = (createState as CreateGroupState.Error).message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    createState = CreateGroupState.Idle
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Try Again")
                            }
                        }

                        else -> { /* Idle state handled outside dialog */
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
                    modifier = Modifier.responsive(mainColumnModifier),
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
                                text = "Create Assignment Group",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            OutlinedTextField(
                                value = groupName,
                                onValueChange = { groupName = it },
                                label = { Text("Group name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

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
                                                    Icons.Default.Add,
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
                                        createState = CreateGroupState.Loading
                                        coroutineScope.launch {
                                            val result = onCreate(
                                                AssignmentGroupDto(
                                                    name = groupName,
                                                    userIds = selectedUsers.map { it.id },
                                                    assignmentIds = emptyList(),
                                                )
                                            )

                                            createState = when (result) {
                                                is OperationResult.Success -> CreateGroupState.Success(result.result)
                                                is OperationResult.Error -> CreateGroupState.Error(result.message)
                                                else -> {
                                                    CreateGroupState.Idle
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = groupName.isNotEmpty() && selectedUsers.isNotEmpty() && createState is CreateGroupState.Idle
                                ) {
                                    Text("Create")
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
@Preview(widthDp = 1920, heightDp = 1080)
@Preview(widthDp = 1080, heightDp = 1920)
@Preview(widthDp = 720, heightDp = 1280)
fun CreateAssignmentGroupComponentPreview() {
    DefaultPreview {
        CreateAssignmentGroupComponent(
            scope = it,
            onNavigateBack = {},
            onCreate = {
                delay(2000)
                return@CreateAssignmentGroupComponent OperationResult.Error("Network error")
            },
            allUsers = emptyList(),
            onNavigateToCreated = {}
        )
    }
}