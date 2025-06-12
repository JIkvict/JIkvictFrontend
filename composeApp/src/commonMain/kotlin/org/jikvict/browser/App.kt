package org.jikvict.browser

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.di.appModule
import org.jikvict.browser.di.platformModule
import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.NavigableScreen
import org.jikvict.browser.screens.registerNavForScreen
import org.jikvict.browser.screens.registeredScreens
import org.jikvict.browser.theme.DarkTheme
import org.jikvict.browser.theme.JIkvictTypography
import org.jikvict.browser.theme.LightTheme
import org.jikvict.browser.theme.rememberInterFontFamily
import org.jikvict.browser.theme.rememberJetBrainsMonoFontFamily
import org.jikvict.browser.util.SavableStateFlow
import org.jikvict.browser.util.ThemeSwitcher
import org.jikvict.browser.util.ThemeSwitcherProvider
import org.jikvict.browser.util.getTheme
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin


// TODO: Сделать @Register(name) аннотацию чтобы создать список name с обьектами помеченными этой аннотацией

// TODO: Сделать @CreateNav аннотацию чтобы создать object HomeScreenRegistrar : ScreenRegistrar<HomeScreen> by createRegistrar() для каждого class помеченого этой аннотацией

private var isKoinStarted = false

@Preview
@Composable
fun App(navController: NavHostController, onNavHostReady: (Boolean) -> Unit = {}) {
    val fonts = JIkvictTypography(rememberInterFontFamily(), rememberJetBrainsMonoFontFamily())
    val themeToSet = if (getTheme()) DarkTheme else LightTheme
    val theme = remember { mutableStateOf(themeToSet) }
    val colors = remember { mutableStateOf(if (getTheme()) DarkColors else LightColors) }
    val themeSwitcher = remember { ThemeSwitcher(theme, colors) }
    if (!isKoinStarted) {
        startKoin {
            modules(platformModule, appModule)
        }
        isKoinStarted = true
    }
    KoinContext {
        MaterialTheme(
            colorScheme = theme.value.colorScheme(),
            typography = fonts.typography
        ) {
            CompositionLocalProvider(
                LocalNavController provides navController,
                ThemeSwitcherProvider provides themeSwitcher,
                LocalAppColors provides colors.value,
            ) {
                DefaultScreen { scope ->
                    NavHost(navController, startDestination = MakeJarScreen()) {
                        registeredScreens.forEach { screen ->
                            registerNavForScreen(screen, scope)
                        }
                    }
                    onNavHostReady(true)
                }

            }
        }
    }
}


val LocalNavController =
    staticCompositionLocalOf<NavHostController> {
        error("No NavController provided")
    }
