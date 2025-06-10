package org.jikvict.browser.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.jikvict.browser.components.DefaultScreenScope
import kotlin.reflect.KClass

interface ScreenRegistrar<T : NavigableScreen> {
    @Composable
    fun UseNavEntry(
        scope: AnimatedContentScope,
        entry: NavBackStackEntry,
        defaultScope: DefaultScreenScope
    ) {
        val block = provideRegisterFunction()
        scope.block(entry, defaultScope)
    }
    fun getType(): KClass<T>

    fun provideRegisterFunction(): @Composable (AnimatedContentScope.(NavBackStackEntry, DefaultScreenScope) -> Unit) {
        return { entry, scope ->
            val route = entry.toRoute<T>(getType())
            route.compose(scope)
        }
    }
}


inline fun <reified T: NavigableScreen> createRegistrar(): ScreenRegistrar<T> {
    return object : ScreenRegistrar<T> {
        override fun getType(): KClass<T> {
            println("Creating registrar for ${T::class.simpleName}")
            return T::class
        }
    }
}

fun NavGraphBuilder.registerNavForScreen(screenRegistrar: ScreenRegistrar<out NavigableScreen>, scope: DefaultScreenScope) {
    composable(
        route = screenRegistrar.getType(),
        enterTransition = { fadeIn(animationSpec = tween(100)) },
        exitTransition = { fadeOut(animationSpec = tween(100)) },
        popEnterTransition = { fadeIn(animationSpec = tween(100)) },
        popExitTransition = { fadeOut(animationSpec = tween(100)) },

        ) {
        screenRegistrar.UseNavEntry(this, it, scope)
    }
}