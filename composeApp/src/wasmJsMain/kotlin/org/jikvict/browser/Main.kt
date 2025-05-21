package org.jikvict.browser

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import org.jikvict.browser.screens.NotFoundScreen

@Suppress("unused")
fun log(message: String): Unit = js("console.log(message)")

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalBrowserHistoryApi::class,
    ExperimentalSerializationApi::class
)
fun main() {
    val body = document.body ?: return
    ComposeViewport(body) {
        val navController = rememberNavController()
        App(navController)
        LaunchedEffect(Unit) {
            val initRoute = window.location.hash.substringAfter('#', "")

            val matchingScreen =
                GeneratedScreenRegistry.allScreens.find { screen ->
                    initRoute.startsWith(screen.getRoute())
                }
            if (matchingScreen != null) {
                navController.navigate(matchingScreen)
            } else {
                log("Not Found")
                navController.navigate(NotFoundScreen)
            }

            window.bindToNavigation(navController) { entry ->
                val route = entry.destination.route.orEmpty()
                return@bindToNavigation "#" +
                    (
                        GeneratedScreenRegistry.allScreens.firstOrNull {
                            route.startsWith(it.getRoute())
                        }
                            ?: NotFoundScreen
                        ).getRoute()
            }
        }
    }
}
