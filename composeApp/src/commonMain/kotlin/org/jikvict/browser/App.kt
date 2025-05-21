package org.jikvict.browser

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.theme.DarkTheme


@Preview
@Composable
fun App(navController: NavHostController) {
    MaterialTheme(
        colorScheme = DarkTheme.colorScheme
    ) {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(navController, startDestination = StartScreen) {
                composable<StartScreen>(
                    enterTransition = { fadeIn(animationSpec = tween(100)) },
                    exitTransition = { fadeOut(animationSpec = tween(100)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(100)) }
                ) {
                    DefaultScreen {
                        Text("Not Found")
                    }
                }
                composable<NotFoundScreen>(
                    enterTransition = { fadeIn(animationSpec = tween(100)) },
                    exitTransition = { fadeOut(animationSpec = tween(100)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(100)) }
                ) {

                    DefaultScreen {
                        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                            Text("Start Screen", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@Serializable
@SerialName("start")
data object StartScreen

@Serializable
@SerialName("not-found")
data object NotFoundScreen