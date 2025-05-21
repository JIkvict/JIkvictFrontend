package org.jikvict.browser.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.serializer

val locations = mutableListOf<NavGraphBuilder.() -> Unit>()

inline fun <reified T : NavigableScreen> registerNavForScreen(screen: NavigableScreen): NavigableScreen {
    locations.add {
        composable<T>(
            enterTransition = { fadeIn(animationSpec = tween(100)) },
            exitTransition = { fadeOut(animationSpec = tween(100)) },
            popEnterTransition = { fadeIn(animationSpec = tween(100)) },
            popExitTransition = { fadeOut(animationSpec = tween(100)) },
        ) {
            screen.compose()
        }
    }
    return object : NavigableScreen by screen {
        override fun getRoute(): String = serializer<T>().descriptor.serialName.lowercase()
    }
}

fun NavGraphBuilder.applyNavigation(locations: List<NavGraphBuilder.() -> Unit>) {
    locations.forEach { location ->
        location()
    }
}
