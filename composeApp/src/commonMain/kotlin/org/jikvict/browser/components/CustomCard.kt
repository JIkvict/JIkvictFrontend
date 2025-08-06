package org.jikvict.browser.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jikvict.browser.util.responsive.adaptiveValue

@Composable
fun CustomCard(
    imageVector: ImageVector,
    text: String,
) {
    val iconSize = adaptiveValue(128.dp, 256.dp)
    val minTextSize = 8.sp
    val maxTextSize = 24.sp
    val lineHeight = adaptiveValue(24.sp, maxTextSize * 1.5)
    FeedCard(
        primaryContent = {
            Box(
                modifier = Modifier.size(iconSize).padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    imageVector = imageVector,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxSize(0.5f),
                )
            }
        },
        secondaryContent = {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = lineHeight,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                autoSize = TextAutoSize.StepBased(minTextSize, maxTextSize),
            )
        },
    )
}
