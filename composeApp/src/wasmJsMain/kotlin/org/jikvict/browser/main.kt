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
            when {
                initRoute.startsWith("start") -> {
                    log("Start")
                    navController.navigate(StartScreen)
                }

                initRoute.startsWith("home") -> {
                    log("Home")
                    navController.navigate(StartScreen)
                }

                else -> {
                    log("Not Found")
                    navController.navigate(NotFoundScreen)
                }
            }
            window.bindToNavigation(navController) { entry ->
                val route = entry.destination.route.orEmpty()
                when {
                    route.startsWith(StartScreen.serializer().descriptor.serialName) -> {
                        "#start"
                    }

                    else -> "#not-found"
                }
            }
        }
    }
}
