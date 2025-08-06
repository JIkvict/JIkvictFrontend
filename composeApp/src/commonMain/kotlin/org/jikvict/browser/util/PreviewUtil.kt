package org.jikvict.browser.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.di.appModule
import org.jikvict.browser.theme.DarkTheme
import org.jikvict.browser.theme.LightTheme
import org.koin.core.context.startKoin

private var isKoinStarted = false

@Composable
fun DefaultPreview(
    isDark: Boolean = true,
    content: @Composable (DefaultScreenScope) -> Unit,
) {
    val theme =
        if (isDark) {
            DarkTheme
        } else {
            LightTheme
        }
    if (!isKoinStarted) {
        startKoin {
            modules(appModule)
        }
        isKoinStarted = true
    }

    MaterialTheme(colorScheme = theme.colorScheme()) {
        val navController = rememberNavController()
        val themeSwitcher = PreviewThemeSwitcher(isDark)
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalThemeSwitcherProvider provides themeSwitcher,
            LocalAppColors provides if (isDark) DarkColors else LightColors,
        ) {
            DefaultScreen {
                content(it)
            }
        }
    }
}

class PreviewThemeSwitcher(
    var isDarkT: Boolean,
) : IThemeSwitcher {
    override val isDark: State<Boolean>
        get() = mutableStateOf(isDarkT)

    override fun switchTheme() {
    }
}
