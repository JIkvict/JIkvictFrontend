package org.jikvict.browser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "",
            undecorated = true,
        ) {
            val navController = rememberNavController()
            App(navController)
        }
    }
}