package org.jikvict.browser

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import kotlinx.browser.document
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import org.jikvict.browser.util.getInitScreen

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalBrowserHistoryApi::class,
    ExperimentalSerializationApi::class,
    InternalSerializationApi::class,
)
fun main() {
    val body = document.body ?: return
    body.style.minWidth = "200px"

    body.style.minHeight = "200px"
    ComposeViewport(body) {
        val navController = rememberNavController()
        var isNavHostReady by remember { mutableStateOf(false) }

        App(navController) { ready ->
            isNavHostReady = ready
        }

        LaunchedEffect(isNavHostReady) {
            if (!isNavHostReady) {
                return@LaunchedEffect
            }
            val initRoute = getInitScreen()

            navController.navigate(initRoute)

            navController.bindToBrowserNavigation()
        }
    }
}
