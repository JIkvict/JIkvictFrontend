package org.jikvict.browser.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.jikvict.browser.util.responsive.Breakpoint
import org.jikvict.browser.util.responsive.ResponsiveModifierBuilder
import org.jikvict.browser.util.responsive.ResponsiveValueBuilder

val mainColumnModifier =
    ResponsiveModifierBuilder {
        base {
            Modifier.wrapContentHeight()
        }
        Breakpoint.SM {
            Modifier.fillMaxWidth(0.9f)
        }
        Breakpoint.MD {
            Modifier.fillMaxWidth(0.55f)
        }
        Breakpoint.LG {
            Modifier.fillMaxWidth(0.5f)
        }
    }

val minTextSize =
    ResponsiveValueBuilder {
        Breakpoint.SM { 20.sp }
        Breakpoint.MD { 30.sp }
        Breakpoint.LG { 60.sp }
    }
val maxTextSize =
    ResponsiveValueBuilder {
        Breakpoint.SM { 60.sp }
        Breakpoint.MD { 60.sp }
        Breakpoint.LG { 116.sp }
    }
