package org.jikvict.browser.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.TasksScreen
import org.jikvict.browser.screens.TasksScreenRouterRegistrar
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val themeSwitcher = ThemeSwitcherProvider.current
    val coroutineScope = rememberCoroutineScope()

    val darkPurple = LocalAppColors.current.Purple6
    val lightPurple = LocalAppColors.current.Purple3
    val theme by themeSwitcher.isDark
    val purple = if (theme) darkPurple else lightPurple

    val moonTint = if (!theme) Color.Black else Color.Unspecified

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(35.dp),
        ) {
            IconComponent(Ijlogo, hoverable = true, tint = purple, onClick = {
                navController.navigate(MakeJarScreen())
            })

            AnimatedIconComponent(
                animationPath = "files/code-animation.json",
                hoverable = true,
                onClick = {
                    if (!TasksScreenRouterRegistrar.matchRoute(
                            navController.currentBackStackEntry?.destination?.route ?: ""
                        )
                    ) {
                        coroutineScope.launch {
                            delay(150)
                            navController.navigate(TasksScreen())
                        }
                    }
                },
                animationType = AnimationType.TOGGLE,
                tint = MaterialTheme.colorScheme.onSurface,
                speed = 2f
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(35.dp),
        ) {
            AnimatedIconComponent(
                animationPath = "files/sun-moon.json",
                hoverable = true,
                tint = moonTint,
                initialProgress = if (theme) 0f else 1f,
                onEnd = {
                    themeSwitcher.switchTheme()
                },
                animationType = AnimationType.TOGGLE,
                speed = 1f
            )

            AnimatedIconComponent(
                animationPath = "files/user-animation.json",
                hoverable = true,
                tint = MaterialTheme.colorScheme.onSurface,
                animationType = AnimationType.ONCE_FORWARD,
                speed = 1f
            )
        }
    }
}

@Preview
@Composable
fun HeaderPreview() {
    DefaultPreview(false) {
        Header()
    }
}
