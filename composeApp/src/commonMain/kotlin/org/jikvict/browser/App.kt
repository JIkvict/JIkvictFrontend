package org.jikvict.browser

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.screens.HomeScreen
import org.jikvict.browser.screens.applyNavigation
import org.jikvict.browser.screens.locations
import org.jikvict.browser.theme.DarkTheme

@Preview
@Composable
fun App(navController: NavHostController) {
    GeneratedScreenRegistry
    MaterialTheme(
        colorScheme = DarkTheme.colorScheme
    ) {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(navController, startDestination = HomeScreen) {
                applyNavigation(locations)
            }
        }
    }
}

val LocalNavController =
    staticCompositionLocalOf<NavHostController> {
        error("No NavController provided")
    }
