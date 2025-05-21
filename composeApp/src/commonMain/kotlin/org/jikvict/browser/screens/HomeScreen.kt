package org.jikvict.browser.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.annotation.Initialize
import org.jikvict.browser.components.DefaultScreen

@Initialize
@Serializable
@SerialName("home")
data object HomeScreen : NavigableScreen by registerNavForScreen<HomeScreen>(
    object : NavigableScreen {
        override val compose: @Composable (() -> Unit)
            get() = {
                DefaultScreen {
                    Text("Home screen")
                }
            }
    }
)
