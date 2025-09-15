package org.jikvict.browser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle

@Composable
fun TextWithOverflowHiding(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = 1,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var shouldShow by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        if (shouldShow) {
            content()
        }

        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            modifier = Modifier.alpha(0f),
            onTextLayout = { textLayoutResult ->
                shouldShow = !textLayoutResult.hasVisualOverflow
            }
        )
    }
}
