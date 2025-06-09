package org.jikvict.browser.screens

import androidx.compose.runtime.Composable

interface NavigableScreen {
    val compose: @Composable (() -> Unit)
        get() = {

        }
    val largeScreen: @Composable (() -> Unit)
    val smallScreen: @Composable (() -> Unit)
        get() = largeScreen
    val mediumScreen: @Composable (() -> Unit)
        get() = largeScreen
}