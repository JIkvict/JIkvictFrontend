@file:Suppress("unused")

package org.jikvict.browser.util.responsive

import androidx.compose.runtime.Composable

@Composable
fun <T> adaptiveValue(
    small: T,
    medium: T,
    large: T,
): T {
    val responsiveValue =
        ResponsiveValueBuilder {
            Breakpoint.SM { small }
            Breakpoint.MD { medium }
            Breakpoint.LG { large }
        }
    return responsiveValue.toValue()
}

@Composable
fun <T> adaptiveValue(
    small: T,
    large: T,
): T {
    val responsiveValue =
        ResponsiveValueBuilder {
            Breakpoint.SM { small }
            Breakpoint.MD { large }
            Breakpoint.LG { large }
        }
    return responsiveValue.toValue()
}

@Composable
fun <T> adaptiveValue(value: T): T {
    val responsiveValue =
        ResponsiveValueBuilder {
            base { value }
        }
    return responsiveValue.toValue()
}
