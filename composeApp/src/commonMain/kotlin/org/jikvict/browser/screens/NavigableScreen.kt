package org.jikvict.browser.screens

import androidx.compose.runtime.Composable

interface NavigableScreen {
    val compose: @Composable (() -> Unit)
}