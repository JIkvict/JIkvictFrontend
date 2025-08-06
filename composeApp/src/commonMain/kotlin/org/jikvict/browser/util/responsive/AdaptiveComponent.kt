@file:Suppress("unused")

package org.jikvict.browser.util.responsive

import androidx.compose.runtime.Composable

class AdaptiveComponentScope<T> {
    private var baseContent: (@Composable () -> T)? = null
    private val breakpointContents = mutableMapOf<Breakpoint, @Composable () -> T>()

    fun base(content: @Composable () -> T) {
        baseContent = content
    }

    operator fun Breakpoint.invoke(content: @Composable () -> T) {
        breakpointContents[this] = content
    }

    @Composable
    internal fun build(): T {
        val currentBreakpoint = Breakpoint.current()

        return Breakpoint.entries
            .filter { it.minWidth <= currentBreakpoint.minWidth }
            .maxByOrNull { it.minWidth }
            ?.let { breakpointContents[it]?.invoke() }
            ?: baseContent?.invoke()
            ?: throw IllegalStateException("No base content provided for AdaptiveComponentBuilder")
    }
}

class AdaptiveComponentBuilder<T> {
    private val scope = AdaptiveComponentScope<T>()

    operator fun invoke(init: AdaptiveComponentScope<T>.() -> Unit): AdaptiveComponentScope<T> = scope.apply(init)

    companion object {
        operator fun <T> invoke(init: AdaptiveComponentScope<T>.() -> Unit): AdaptiveComponentBuilder<T> {
            val builder = AdaptiveComponentBuilder<T>()
            builder.scope.init()
            return builder
        }
    }

    @Composable
    fun build(): T = scope.build()
}

@Composable
fun <T> adaptiveComponent(builder: AdaptiveComponentScope<T>.() -> Unit): T {
    val scope = AdaptiveComponentScope<T>().apply(builder)
    return scope.build()
}

@Composable
fun <T> adaptiveComponent(
    small: @Composable () -> T,
    medium: @Composable () -> T,
    large: @Composable () -> T,
): T =
    adaptiveComponent {
        Breakpoint.SM { small() }
        Breakpoint.MD { medium() }
        Breakpoint.LG { large() }
    }

@Composable
fun <T> adaptiveComponent(
    small: @Composable () -> T,
    large: @Composable () -> T,
): T =
    adaptiveComponent {
        Breakpoint.SM { small() }
        Breakpoint.MD { large() }
        Breakpoint.LG { large() }
    }
