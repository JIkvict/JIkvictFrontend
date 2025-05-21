package org.jikvict.browser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DefaultScreen(content: @Composable BoxScope.() -> Unit) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Header()
        Box(
            modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }
        Footer()
    }
}

@Preview
@Composable
fun DefaultScreenPreview() {
    DefaultScreen {
    }
}
