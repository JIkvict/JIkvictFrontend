package org.jikvict.browser.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import org.jikvict.browser.theme.maxTextSize
import org.jikvict.browser.theme.minTextSize

@Composable
fun IlluminatingText(
    text: String,
    minTextSize: TextUnit,
    maxTextSize: TextUnit,
    color: Color,
    shadowColor: Color = color.copy(alpha = 0.75f),
) {
    AutoSizeText(
        text = text,
        style =
            MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                shadow =
                    Shadow(
                        color = shadowColor,
                        offset = Offset(1f, 1f),
                        blurRadius = 32f,
                    ),
            ),
        color = color,
        softWrap = false,
        minTextSize = minTextSize,
        maxTextSize = maxTextSize,
    )
}

@Composable
fun IlluminatingText(
    text: String,
    textSize: TextUnit,
    color: Color,
    shadowColor: Color = color.copy(alpha = 0.75f),
) {
    AutoSizeText(
        text = text,
        style =
            MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                shadow =
                    Shadow(
                        color = shadowColor,
                        offset = Offset(1f, 1f),
                        blurRadius = 32f,
                    ),
            ),
        color = color,
        softWrap = false,
        minTextSize = textSize,
        maxTextSize = textSize,
    )
}

@Composable
fun IlluminatingText(
    text: String,
    minTextSize: TextUnit,
    maxTextSize: TextUnit,
    color: Color,
    shadow: Shadow,
) {
    AutoSizeText(
        text = text,
        style =
            MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                shadow = shadow,
            ),
        color = color,
        softWrap = false,
        minTextSize = minTextSize,
        maxTextSize = maxTextSize,
    )
}
