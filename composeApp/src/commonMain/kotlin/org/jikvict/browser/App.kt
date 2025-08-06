package org.jikvict.browser

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.di.appModule
import org.jikvict.browser.di.platformModule
import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.registerNavForScreen
import org.jikvict.browser.screens.registeredScreens
import org.jikvict.browser.theme.DarkTheme
import org.jikvict.browser.theme.JIkvictTypography
import org.jikvict.browser.theme.LightTheme
import org.jikvict.browser.theme.rememberInterFontFamily
import org.jikvict.browser.theme.rememberJetBrainsMonoFontFamily
import org.jikvict.browser.util.LocalThemeSwitcherProvider
import org.jikvict.browser.util.ThemeSwitcher
import org.jikvict.browser.util.getTheme
import org.koin.core.context.startKoin

// TODO: Сделать @Register(name) аннотацию чтобы создать список name с обьектами помеченными этой аннотацией

// TODO: Сделать @CreateNav аннотацию чтобы создать object HomeScreenRegistrar : ScreenRegistrar<HomeScreen> by createRegistrar() для каждого class помеченого этой аннотацией

private var isKoinStarted = false

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    navController: NavHostController,
    onNavHostReady: (Boolean) -> Unit = {},
) {
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
    MaterialExpressiveTheme(
        colorScheme = theme.value.colorScheme(),
        typography = fonts.typography,
        motionScheme = MotionScheme.expressive(),
    ) {
        val motionScheme = MaterialTheme.motionScheme
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalThemeSwitcherProvider provides themeSwitcher,
            LocalAppColors provides colors.value,
        ) {
            DefaultScreen { scope ->
                LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
                    scope.verticalScroll.scrollTo(0)
                }
                NavHost(navController, startDestination = MakeJarScreen()) {
                    registeredScreens.forEach { screen ->
                        registerNavForScreen(screen, scope, motionScheme)
                    }
                }
                onNavHostReady(true)
            }
        }
    }
}

val LocalNavController =
    staticCompositionLocalOf<NavHostController> {
        error("No NavController provided")
    }
