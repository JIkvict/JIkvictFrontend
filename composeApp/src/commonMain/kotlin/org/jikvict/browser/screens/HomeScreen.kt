package org.jikvict.browser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.annotation.Initialize
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.theme.DarkTheme

@Composable
private fun HomeScreen() {
    DefaultScreen {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            repeat(100) {
                Text("Home Screen $it", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Initialize
@Serializable
@SerialName("home")
data object HomeScreen : NavigableScreen by registerNavForScreen<HomeScreen>(
    object : NavigableScreen {
        override val compose: @Composable (() -> Unit)
            get() = {
                HomeScreen()
            }
    },
)

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme(colorScheme = DarkTheme.colorScheme) {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavController provides navController) {
            HomeScreen()
        }
    }
}
