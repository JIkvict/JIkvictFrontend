package org.jikvict.browser.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.constant.ThemeColors
import org.jikvict.browser.theme.CustomTheme
import org.jikvict.browser.theme.DarkTheme
import org.jikvict.browser.theme.LightTheme

class ThemeSwitcher(
    var theme: MutableState<CustomTheme>,
    var localColors: MutableState<ThemeColors>
) : IThemeSwitcher {

    private val _isDark = derivedStateOf { theme.value == DarkTheme }
    override val isDark: State<Boolean> get() = _isDark

    override fun switchTheme() {
        theme.value = if (theme.value == DarkTheme) LightTheme else DarkTheme
        localColors.value = if (theme.value == DarkTheme) DarkColors else LightColors
    }
}
val ThemeSwitcherProvider =
    staticCompositionLocalOf<IThemeSwitcher> {
        error("No ThemeSwitcher provided")
    }

expect fun setTheme(isDark: Boolean)
expect fun getTheme(): Boolean
