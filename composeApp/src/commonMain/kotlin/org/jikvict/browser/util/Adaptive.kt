package org.jikvict.browser.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun <T> adaptiveValue(small: T, medium: T, large: T): T {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val width = with(density) { windowInfo.containerSize.width.toDp() }

    return when {
        width < 600.dp -> small
        width < 1200.dp -> medium
        else -> large
    }
}