package org.jikvict.browser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import kotlinx.browser.document
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalBrowserHistoryApi::class,
    ExperimentalSerializationApi::class
)
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}
