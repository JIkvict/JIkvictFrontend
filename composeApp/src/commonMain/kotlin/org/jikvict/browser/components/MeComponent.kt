package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.util.DefaultPreview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MeComponent(
    scope: DefaultScreenScope,
    name: String,
    longLivingToken: StateFlow<String?> = MutableStateFlow(null),
    tokenLoadResult: StateFlow<OperationResult<Unit>?> = MutableStateFlow(null),
    tokenCreateResult: StateFlow<OperationResult<Unit>?> = MutableStateFlow(null),
    tokenDeleteResult: StateFlow<OperationResult<Unit>?> = MutableStateFlow(null),
    viewModelScope: CoroutineScope,
    loadToken: suspend () -> Unit = {},
    createToken: suspend () -> Unit = {},
    deleteToken: suspend () -> Unit = {},
    resetTokenLoadResult: () -> Unit = {},
    resetTokenCreateResult: () -> Unit = {},
    resetTokenDeleteResult: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val token by longLivingToken.collectAsState()
    val loadResult by tokenLoadResult.collectAsState()
    val createResult by tokenCreateResult.collectAsState()
    val deleteResult by tokenDeleteResult.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var copyButtonText by remember { mutableStateOf("Copy") }
    var isTokenVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loadToken()
    }

    LaunchedEffect(createResult) {
        if (createResult is OperationResult.Success) {
            resetTokenCreateResult()
        }
    }

    LaunchedEffect(deleteResult) {
        if (deleteResult is OperationResult.Success) {
            resetTokenDeleteResult()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello, $name",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Long Living Token",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        val isLoading = loadResult is OperationResult.Loading ||
                createResult is OperationResult.Loading ||
                deleteResult is OperationResult.Loading

        if (isLoading && token == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(
                    modifier = Modifier.width(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        } else if (token != null) {
            Card(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Your Token:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    SelectionContainer {
                        Text(
                            text = if (isTokenVisible) token ?: "" else "•".repeat((token?.length ?: 20).coerceAtMost(50)),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(12.dp),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                token?.let {
                                    clipboardManager.setText(AnnotatedString(it))
                                    copyButtonText = "Copied!"
                                    coroutineScope.launch {
                                        delay(2000)
                                        copyButtonText = "Copy"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(copyButtonText)
                        }

                        OutlinedButton(
                            onClick = { isTokenVisible = !isTokenVisible },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                        ) {
                            Icon(
                                imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isTokenVisible) "Hide" else "Show",
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTokenVisible) "Hide" else "Show")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModelScope.launch {
                                    deleteToken()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "You don't have a long living token yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Button(
                        onClick = {
                            viewModelScope.launch {
                                createToken()
                            }
                        },
                        enabled = !isLoading,
                    ) {
                        Text("Create Token")
                    }
                }
            }
        }

        Box(
            modifier = Modifier.height(24.dp),
        ) {
            when (val res = createResult) {
                is OperationResult.Error -> {
                    if (!isLoading) {
                        Text(
                            text = res.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                else -> Unit
            }

            when (val res = deleteResult) {
                is OperationResult.Error -> {
                    if (!isLoading) {
                        Text(
                            text = res.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                else -> Unit
            }

            when (val res = loadResult) {
                is OperationResult.Error -> {
                    if (!isLoading) {
                        Text(
                            text = res.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                else -> Unit
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(0.5f),
        ) {
            Text("Log out")
        }
    }
}

@Preview(widthDp = 1980, heightDp = 1080)
@Composable
fun MeComponentPreview() {
    DefaultPreview {
        val previewScope = rememberCoroutineScope()
        MeComponent(it, "John Doe", viewModelScope = previewScope)
    }
}