package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.annotation.Register
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.IlluminatingText
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.theme.mainColumnModifier
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.Breakpoint
import org.jikvict.browser.util.responsive.ResponsiveValueBuilder
import org.jikvict.browser.util.responsive.responsive
import org.jikvict.browser.viewmodel.LoginScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@Composable
fun LoginScreenComposable(defaultScreenScope: DefaultScreenScope) {
    val viewModel = koinViewModel<LoginScreenViewModel>()
    LoginScreenComposable(
        defaultScreenScope,
        viewModel.aisId,
        viewModel.password,
        viewModel::setAisId,
        viewModel::setPassword,
        viewModel.viewModelScope,
        viewModel.loginResult,
        viewModel::login,
        viewModel::resetLoginResult
    )
}

@Composable
fun LoginScreenComposable(
    defaultScreenScope: DefaultScreenScope,
    aisId: StateFlow<String> = MutableStateFlow(""),
    password: StateFlow<String> = MutableStateFlow(""),
    setAisId: (String) -> Unit = {},
    setPassword: (String) -> Unit = {},
    viewModelScope: CoroutineScope,
    loginResult: StateFlow<OperationResult<Unit>?> = MutableStateFlow(null),
    login: suspend () -> Unit = {},
    resetLoginResult: () -> Unit = {},
) =
    with(defaultScreenScope) {
        val navController = LocalNavController.current
        val loginTextSize =
            ResponsiveValueBuilder {
                Breakpoint.SM { 30.sp }
                Breakpoint.MD { 45.sp }
                Breakpoint.LG { 60.sp }
            }


        Box(
            modifier =
                Modifier.fitContentToScreen(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.responsive(mainColumnModifier),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IlluminatingText(
                    "Log In",
                    loginTextSize.toValue(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shadowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                )

                val aisId by aisId.collectAsState()
                val password by password.collectAsState()

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    value = aisId,
                    onValueChange = { raw ->
                        val fixed = raw.uppercase()
                        if (fixed.length > 20) return@OutlinedTextField
                        setAisId(fixed)
                    },
                    label = { Text("Ais ID") },
                )

                val passwordVisible = rememberSaveable { mutableStateOf(false) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    value = password,
                    onValueChange = { raw ->
                        if (raw.length > 40) return@OutlinedTextField
                        setPassword(raw)
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = if (passwordVisible.value) KeyboardType.Text else KeyboardType.Password,
                        ),
                    visualTransformation =
                        if (passwordVisible.value) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    trailingIcon = {
                        val icon = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        val description = if (passwordVisible.value) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = icon, contentDescription = description)
                        }
                    },
                )
                Button(
                    onClick = {
                        viewModelScope.launch {
                            login()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.5f),
                ) {
                    Text("Let's go")
                }
                val loginResult by loginResult.collectAsState()

                when (val res = loginResult) {
                    is OperationResult.Error -> {
                        Text(
                            text = res.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    is OperationResult.Success -> {
                        resetLoginResult()
                        context(navController) {
                            TasksScreen().navigateTo()
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

@Preview(widthDp = 4000, heightDp = 3000)
@Composable
private fun LoginScreenComposablePreviewSM() {
    DefaultPreview {
        val previewScope = rememberCoroutineScope()
        LoginScreenComposable(it, viewModelScope = previewScope)
    }
}


@Register
@Serializable
@SerialName("login")
data object LoginScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { LoginScreenComposable(it) }
}

object LoginScreenRouterRegistrar : ScreenRouterRegistrar<LoginScreen> {
    override val screen: KClass<LoginScreen>
        get() = LoginScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen = LoginScreen
}

object LoginScreenRegistrar : ScreenRegistrar<LoginScreen> by createRegistrar()
