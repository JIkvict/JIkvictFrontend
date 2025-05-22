package org.jikvict.browser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.annotation.Initialize
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.util.DefaultPreview

@Composable
private fun HomeScreen() {
    DefaultScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
    DefaultPreview {
        HomeScreen()
    }
}
