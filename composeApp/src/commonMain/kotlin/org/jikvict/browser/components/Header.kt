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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.icons.myiconpack.Code
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.icons.myiconpack.User
import org.jikvict.browser.screens.HomeScreen
import org.jikvict.browser.screens.NotFoundScreen
import org.jikvict.browser.theme.DarkTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header() {
    val navController = LocalNavController.current

    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(35.dp)
        ) {
            IconComponent(Ijlogo)
            IconComponent(Code, hoverable = true, onClick = {
                navController.navigate(NotFoundScreen)
            })
        }

        Spacer(modifier = Modifier.weight(1f))
        IconComponent(User, hoverable = true, onClick = {
            navController.navigate(HomeScreen)
        })
    }
}

@Preview
@Composable
fun PreviewHeader() {
    MaterialTheme(colorScheme = DarkTheme.colorScheme) {
        Header()
    }
}
