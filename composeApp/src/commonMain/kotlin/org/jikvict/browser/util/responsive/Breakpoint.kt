package org.jikvict.browser.util.responsive

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Breakpoint(
    val minWidth: Dp,
) {
    SM(0.dp),
    MD(600.dp),
    LG(1200.dp),
    ;

    companion object {
        @Composable
        fun current(): Breakpoint {
            val windowInfo = LocalWindowInfo.current
            val density = LocalDensity.current
            val width = with(density) { windowInfo.containerSize.width.toDp() }

            return when {
                width < MD.minWidth -> SM
                width < LG.minWidth -> MD
                else -> LG
            }
        }
    }
}
