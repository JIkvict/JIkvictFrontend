package org.jikvict.browser

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import org.jikvict.browser.screens.NotFoundScreen
import org.jikvict.browser.screens.routers

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalBrowserHistoryApi::class,
    ExperimentalSerializationApi::class, InternalSerializationApi::class
)
fun main() {
    val body = document.body ?: return
    ComposeViewport(body) {
        val navController = rememberNavController()
        App(navController)
        LaunchedEffect(Unit) {
            val initRoute = window.location.hash.substringAfter('#', "").substringBefore("/?")

            val paramsRaw = window.location.hash.substringAfter('?', "")
            val splitParams = paramsRaw.split("&").filter { it.isNotBlank() }

            val params = if (splitParams.isEmpty()) {
                emptyMap()
            } else {
                paramsRaw.split("&").associate {
                    val (key, value) = it.split("=", limit = 2)
                    key to value.ifEmpty { null }
                }
            }
            val router = routers.firstOrNull { it.matchRoute(initRoute) }
            if (router != null) {
                val screen = router.constructScreen(params)
                navController.navigate(screen)
            } else {
                navController.navigate(NotFoundScreen())
            }

            window.bindToNavigation(navController) { entry ->
                println("Binding to navigation: ${entry.destination.route}")
                var mapping = mapOf<String, Any?>()
                entry.arguments?.read {
                    mapping = this.toMap()
                }

                val baseRoute = entry.destination.route?.substringBefore("?")?.substringBefore("{") ?: ""

                val queryParams = if (mapping.isNotEmpty()) {
                    mapping.entries.joinToString(prefix = "?", separator = "&") { (key, value) ->
                        "$key=${value?.toString() ?: ""}"
                    }
                } else {
                    ""
                }

                return@bindToNavigation "#$baseRoute$queryParams"
            }
        }
    }
}
