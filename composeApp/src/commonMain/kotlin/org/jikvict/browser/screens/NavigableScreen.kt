package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import org.jikvict.browser.components.DefaultScreenScope

interface NavigableScreen {
    val compose: @Composable ((DefaultScreenScope) -> Unit)
        get() = largeScreen
    val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
    val smallScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = largeScreen
    val mediumScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = largeScreen

    val requiredRoles: List<String>
        get() = emptyList()
}

interface TeacherScreen : NavigableScreen {
    override val requiredRoles: List<String>
        get() = listOf("TEACHER")
}

context(navController: NavHostController)
fun NavigableScreen.navigateTo() {
    val router = routers.first { it.screen == this::class }
    if (!router.matchRoute(navController.currentBackStackEntry?.destination?.route ?: "")) {
        navController.navigate(this)
    }
}

fun NavHostController.navigateBackOr(screen: NavigableScreen) =
    if (popBackStack()) {
        true
    } else {
        screen.forceNavigateTo()
        true
    }

context(navController: NavHostController)
fun NavigableScreen.forceNavigateTo() {
    navController.navigate(this)
}
