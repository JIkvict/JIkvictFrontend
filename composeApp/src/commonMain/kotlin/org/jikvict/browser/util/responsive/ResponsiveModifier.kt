@file:Suppress("unused")

package org.jikvict.browser.util.responsive

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class ResponsiveModifierScope {
    private var baseModifier: Modifier.() -> Modifier = { this }
    private val breakpointModifiers = mutableMapOf<Breakpoint, Modifier.() -> Modifier>()

    fun base(modifier: Modifier.() -> Modifier) {
        baseModifier = modifier
    }

    operator fun Breakpoint.invoke(modifier: Modifier.() -> Modifier) {
        breakpointModifiers[this] = modifier
    }

    @Composable
    internal fun build(): Modifier {
        var result = Modifier.baseModifier()
        val currentBreakpoint = Breakpoint.current()

        for (breakpoint in Breakpoint.entries) {
            if (breakpoint.minWidth <= currentBreakpoint.minWidth) {
                breakpointModifiers[breakpoint]?.let { modifierFn ->
                    result = result.modifierFn()
                }
            }
        }

        return result
    }
}

class ResponsiveModifierBuilder {
    private val scope = ResponsiveModifierScope()

    operator fun invoke(init: ResponsiveModifierScope.() -> Unit): ResponsiveModifierScope = scope.apply(init)

    companion object Companion {
        operator fun invoke(init: ResponsiveModifierScope.() -> Unit): ResponsiveModifierBuilder {
            val style = ResponsiveModifierBuilder()
            style.scope.init()
            return style
        }
    }

    @Composable
    fun toModifier(): Modifier = scope.build()
}

@Composable
fun Modifier.responsive(responsive: ResponsiveModifierBuilder): Modifier = this.then(responsive.toModifier())

@Composable
fun responsiveModifier(builder: ResponsiveModifierScope.() -> Unit): Modifier {
    val scope = ResponsiveModifierScope().apply(builder)
    return scope.build()
}
