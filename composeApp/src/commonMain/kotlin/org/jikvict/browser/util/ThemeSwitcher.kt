package org.jikvict.browser.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf
import org.jikvict.browser.theme.CustomTheme
import org.jikvict.browser.theme.DarkTheme
import org.jikvict.browser.theme.LightTheme

open class ThemeSwitcher(var theme: MutableState<CustomTheme>): IThemeSwitcher {
    override val isDark: Boolean
        get() = getTheme()

    override fun switchTheme() {
        theme.value = if (theme.value == DarkTheme) {
            LightTheme
        } else {
            DarkTheme
        }
        setTheme(!isDark)
    }
}

val ThemeSwitcherProvider =
    staticCompositionLocalOf<IThemeSwitcher> {
        error("No ThemeSwitcher provided")
    }

expect fun setTheme(isDark: Boolean)
expect fun getTheme(): Boolean
