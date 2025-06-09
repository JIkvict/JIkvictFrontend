package org.jikvict.browser.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import jikvictfrontend.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.icons.myiconpack.Code
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.NotFoundScreen
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val themeSwitcher = ThemeSwitcherProvider.current

    val darkPurple = LocalAppColors.current.Purple6
    val lightPurple = LocalAppColors.current.Purple3
    val theme by themeSwitcher.isDark
    val purple = if (theme) darkPurple else lightPurple

    val themeAnimatedIcon by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/sun-moon.json").decodeToString()
        )
    }
    val userAnimatedIcon by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/user-animation.json").decodeToString()
        )
    }

    var isThemeAnimating by remember { mutableStateOf(false) }
    var themeProgress by remember { mutableFloatStateOf(if (theme) 0f else 1f) }

    var isUserAnimating by remember { mutableStateOf(false) }
    var userProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(theme) {
        if (!isThemeAnimating) {
            themeProgress = if (theme) 0f else 1f
        }
    }

    LaunchedEffect(isThemeAnimating) {
        if (isThemeAnimating) {
            val targetProgress = if (themeProgress < 0.5f) 1f else 0f
            val startProgress = themeProgress

            val steps = 30
            val stepDuration = 16L
            val stepSize = (targetProgress - startProgress) / steps

            repeat(steps) { step ->
                themeProgress = startProgress + stepSize * (step + 1)
                delay(stepDuration)
            }

            themeProgress = targetProgress

            themeSwitcher.switchTheme()

            isThemeAnimating = false
        }
    }

    LaunchedEffect(isUserAnimating) {
        if (isUserAnimating) {
            val steps = 30
            val stepDuration = 16L

            repeat(steps) { step ->
                userProgress = step / (steps - 1f)
                delay(stepDuration)
            }

            userProgress = 1f

            repeat(steps) { step ->
                userProgress = 1f - (step / (steps - 1f))
                delay(stepDuration)
            }

            userProgress = 0f

            isUserAnimating = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            IconComponent(Code, hoverable = true, onClick = {
                println("I was clicked")
                navController.navigate(NotFoundScreen())
            })

        }

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(35.dp),
        ) {
            IconComponent(
                rememberLottiePainter(
                    composition = themeAnimatedIcon,
                    progress = { themeProgress }
                ), hoverable = true, tint = MaterialTheme.colorScheme.onSurface, onClick = {
                    if (!isThemeAnimating) {
                        isThemeAnimating = true
                    }
                })

            IconComponent(
                rememberLottiePainter(
                    composition = userAnimatedIcon,
                    progress = { userProgress }
                ), hoverable = true, tint = MaterialTheme.colorScheme.onSurface, onClick = {
                    if (!isUserAnimating) {
                        isUserAnimating = true
                    }
                })
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
