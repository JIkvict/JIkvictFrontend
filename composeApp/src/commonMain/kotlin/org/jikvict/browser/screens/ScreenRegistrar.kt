package org.jikvict.browser.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
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
        defaultScope: DefaultScreenScope,
    ) {
        val block = provideRegisterFunction()
        scope.block(entry, defaultScope)
    }

    fun getType(): KClass<T>

    fun provideRegisterFunction(): @Composable (AnimatedContentScope.(NavBackStackEntry, DefaultScreenScope) -> Unit) =
        { entry, scope ->
            val route = entry.toRoute<T>(getType())
            route.compose(scope)
        }
}

inline fun <reified T : NavigableScreen> createRegistrar(): ScreenRegistrar<T> =
    object : ScreenRegistrar<T> {
        override fun getType(): KClass<T> = T::class
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun NavGraphBuilder.registerNavForScreen(
    screenRegistrar: ScreenRegistrar<out NavigableScreen>,
    scope: DefaultScreenScope,
    motionScheme: MotionScheme,
) {
    composable(
        route = screenRegistrar.getType(),
        enterTransition = { fadeIn(animationSpec = motionScheme.slowEffectsSpec()) },
        exitTransition = { fadeOut(animationSpec = motionScheme.slowEffectsSpec()) },
        popEnterTransition = { fadeIn(animationSpec = motionScheme.slowEffectsSpec()) },
        popExitTransition = { fadeOut(animationSpec = motionScheme.slowEffectsSpec()) },
    ) {
        screenRegistrar.UseNavEntry(this, it, scope)
    }
}
