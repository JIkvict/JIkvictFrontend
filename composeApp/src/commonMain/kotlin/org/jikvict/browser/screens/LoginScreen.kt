package org.jikvict.browser.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
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
        viewModel::resetLoginResult,
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
) = with(defaultScreenScope) {
    val navController = LocalNavController.current
    val loginTextSize =
        ResponsiveValueBuilder {
            Breakpoint.SM { 30.sp }
            Breakpoint.MD { 45.sp }
            Breakpoint.LG { 60.sp }
        }
    val aisIdFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fitContentToScreen(),
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
            val loginResult by loginResult.collectAsState()

            val isLoading = loginResult is OperationResult.Loading

            fun performLogin() {
                if (!isLoading) {
                    viewModelScope.launch {
                        login()
                    }
                }
            }

            OutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth(0.7f)
                        .focusRequester(aisIdFocusRequester),
                value = aisId,
                onValueChange = { raw ->
                    val filtered = raw.replace(Regex("[\r\n]"), "")
                    val fixed = filtered.uppercase()
                    if (fixed.length > 20) return@OutlinedTextField
                    setAisId(fixed)
                },
                label = { Text("Ais ID") },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        },
                    ),
            )

            val passwordVisible = rememberSaveable { mutableStateOf(false) }
            OutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth(0.7f)
                        .focusRequester(passwordFocusRequester),
                value = password,
                onValueChange = { raw ->
                    val filtered = raw.replace(Regex("[\r\n]"), "")
                    if (filtered.length > 40) return@OutlinedTextField
                    setPassword(filtered)
                },
                label = { Text("Password") },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = if (passwordVisible.value) KeyboardType.Text else KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            performLogin()
                        },
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
                    IconButton(
                        onClick = { passwordVisible.value = !passwordVisible.value },
                        enabled = !isLoading,
                    ) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                },
            )

            AnimatedLoginButton(
                isLoading = isLoading,
                onClick = { performLogin() },
            )

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedLoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val slowSpecEffect = MaterialTheme.motionScheme.slowEffectsSpec<Float>()

    AnimatedContent(
        targetState = isLoading,
        transitionSpec = {
            fadeIn(slowSpecEffect) togetherWith
                fadeOut(slowSpecEffect) using
                SizeTransform { initialSize, _ ->
                    keyframes {
                        durationMillis = 800
                        IntSize(
                            width = if (targetState) (initialSize.width * 0.3f).toInt() else initialSize.width,
                            height = initialSize.height,
                        ) at 400
                    }
                }
        },
        contentAlignment = Alignment.Center,
        label = "login_button_animation",
    ) { loading ->
        if (loading) {
            LoadingIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(0.5f),
            ) {
                Text("Let's go")
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
