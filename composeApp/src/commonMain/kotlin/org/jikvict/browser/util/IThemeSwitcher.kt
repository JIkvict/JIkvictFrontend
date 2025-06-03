package org.jikvict.browser.util

import androidx.compose.runtime.State

interface IThemeSwitcher {
    val isDark: State<Boolean>

    fun switchTheme()
}