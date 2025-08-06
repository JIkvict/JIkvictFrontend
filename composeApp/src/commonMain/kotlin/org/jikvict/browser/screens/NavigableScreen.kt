package org.jikvict.browser.screens

import androidx.compose.runtime.Composable
import org.jikvict.browser.components.DefaultScreenScope

interface NavigableScreen {
    val compose: @Composable ((DefaultScreenScope) -> Unit)
        get() = largeScreen
    val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
    val smallScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = largeScreen
    val mediumScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = largeScreen
}
