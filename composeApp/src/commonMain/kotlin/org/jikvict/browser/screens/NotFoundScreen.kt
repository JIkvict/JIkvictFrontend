package org.jikvict.browser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.annotation.Initialize
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.util.DefaultPreview


@Composable
private fun NotFoundScreen() {
    DefaultScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),

            ) {
            Text("Not Found Screen", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}


@Initialize
@Serializable
@SerialName("not-found")
data object NotFoundScreen : NavigableScreen by registerNavForScreen<NotFoundScreen>(
    object : NavigableScreen {
        override val compose: @Composable (() -> Unit)
            get() = {
                NotFoundScreen()
            }
    },
)

@Preview
@Composable
fun NotFoundScreenPreview() {
    DefaultPreview {
        NotFoundScreen()
    }
}
