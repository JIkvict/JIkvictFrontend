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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.icons.myiconpack.Code
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.icons.myiconpack.Moon
import org.jikvict.browser.icons.myiconpack.Sun
import org.jikvict.browser.icons.myiconpack.User
import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.NotFoundScreen
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header() {
    val navController = LocalNavController.current
    val themeSwitcher = ThemeSwitcherProvider.current

    val darkPurple = LocalAppColors.current.Purple6
    val lightPurple = LocalAppColors.current.Purple3
    val theme by themeSwitcher.isDark
    val purple = if (theme) darkPurple else lightPurple

    val themeIcon = if (theme) Sun else Moon
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 8.dp),
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
                navController.navigate(NotFoundScreen())
            })

        }

        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(35.dp),
        ) {
            IconComponent(themeIcon, hoverable = true, onClick = {
                themeSwitcher.switchTheme()
            })

            IconComponent(User, hoverable = true, onClick = {
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