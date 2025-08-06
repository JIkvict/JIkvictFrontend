@file:Suppress("unused")

package org.jikvict.browser.util.responsive

import androidx.compose.runtime.Composable

class ResponsiveValueScope<T> {
    private var baseValue: T? = null
    private val breakpointValues = mutableMapOf<Breakpoint, T>()

    fun base(value: () -> T) {
        baseValue = value()
    }

    operator fun Breakpoint.invoke(block: ResponsiveValueScope<T>.() -> T) {
        breakpointValues[this] = block()
    }

    @Composable
    internal fun build(): T {
        val currentBreakpoint = Breakpoint.current()

        return Breakpoint.entries
            .filter { it.minWidth <= currentBreakpoint.minWidth }
            .maxByOrNull { it.minWidth }
            ?.let { breakpointValues[it] }
            ?: baseValue
            ?: throw IllegalStateException("No base value provided for ResponsiveValueBuilder")
    }
}

class ResponsiveValueBuilder<T> {
    private val scope = ResponsiveValueScope<T>()

    operator fun invoke(init: ResponsiveValueScope<T>.() -> Unit): ResponsiveValueScope<T> = scope.apply(init)

    companion object {
        operator fun <T> invoke(init: ResponsiveValueScope<T>.() -> Unit): ResponsiveValueBuilder<T> {
            val builder = ResponsiveValueBuilder<T>()
            builder.scope.init()
            return builder
        }
    }

    @Composable
    fun toValue(): T = scope.build()
}

@Composable
fun <T> responsiveValue(builder: ResponsiveValueScope<T>.() -> Unit): T {
    val scope = ResponsiveValueScope<T>().apply(builder)
    return scope.build()
}
