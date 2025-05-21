package org.jikvict.browser.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.theme.DarkTheme

@Composable
fun DefaultScreen(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = screenHeight)
                .verticalScroll(rememberScrollState())
        ) {
            Header()
            content()
            Footer()
        }
    }
}

@Preview
@Composable
fun DefaultScreenPreview() {
    MaterialTheme(colorScheme = DarkTheme.colorScheme) {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavController provides navController) {
            DefaultScreen {

            }
        }
    }
}
