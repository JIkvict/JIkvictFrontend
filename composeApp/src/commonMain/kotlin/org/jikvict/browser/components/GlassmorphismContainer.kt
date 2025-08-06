package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.util.DefaultPreview

/**
 * GlassmorphismContainer is a component that creates a glass-like effect.
 * It implements the glass-morphism design pattern with curved borders,
 * blur effects, and subtle lighting.
 *
 * @param modifier The modifier to be applied to the container
 * @param cornerRadius The corner radius of the container
 * @param blurRadius The blur radius for the container
 * @param backgroundColor The background color of the container
 * @param backgroundAlpha The alpha value for the background (0.0f to 1.0f)
 * @param noiseAlpha The alpha value for the noise effect (0.0f to 1.0f)
 * @param content The content to be displayed inside the container
 */
@Composable
fun GlassmorphismContainer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    blurRadius: Dp = 10.dp,
    backgroundColor: Color = Color.White,
    backgroundAlpha: Float = 0.1f,
    noiseAlpha: Float = 0.01f,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(CurvedBorder(cornerRadius = cornerRadius))
                    .background(backgroundColor.copy(alpha = backgroundAlpha))
                    .blur(
                        radius = blurRadius,
                        edgeTreatment = androidx.compose.ui.draw.BlurredEdgeTreatment.Rectangle,
                    ).drawWithCache {
                        onDrawWithContent {
                            this.drawContent()

                            drawRect(
                                color = Color.White.copy(alpha = noiseAlpha),
                                blendMode = BlendMode.Overlay,
                            )

                            drawRect(
                                brush =
                                    Brush.radialGradient(
                                        colors =
                                            listOf(
                                                Color(0x65F3F0F0),
                                                Color(0x19BBBABA),
                                            ),
                                        center =
                                            Offset(
                                                size.width * 0.2f,
                                                size.height * 0.5f,
                                            ),
                                        radius = size.width.coerceAtLeast(size.height) * 0.8f,
                                    ),
                                blendMode = BlendMode.Luminosity,
                            )

                            drawPath(
                                brush =
                                    Brush.linearGradient(
                                        colors =
                                            listOf(
                                                Color(0x80FFFFFF),
                                                Color(0x65AAA7A7),
                                            ),
                                        start = Offset(size.width * 0.8f, size.height * 0.2f),
                                        end = Offset(size.width, size.height),
                                    ),
                                path = blurPath(size, cornerRadius.toPx()),
                                style = Stroke(1.5.dp.toPx()),
                                blendMode = BlendMode.Luminosity,
                            )
                        }
                    },
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            content = content,
        )
    }
}

/**
 * A custom shape for the glass container with curved borders
 */
class CurvedBorder(
    private val cornerRadius: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): androidx.compose.ui.graphics.Outline {
        val cornerRadius = with(density) { cornerRadius.toPx() }
        return androidx.compose.ui.graphics.Outline.Generic(
            path = blurPath(size = size, cornerRadius = cornerRadius),
        )
    }
}

/**
 * Creates a path for the curved border shape
 */
private fun blurPath(
    size: Size,
    cornerRadius: Float,
): Path =
    Path().apply {
        // Start from top-left corner
        moveTo(cornerRadius, 0f)

        // Top edge to top-right corner
        lineTo(size.width - cornerRadius, 0f)

        // Top-right corner
        arcTo(
            rect =
                Rect(
                    left = size.width - cornerRadius * 2,
                    top = 0f,
                    right = size.width,
                    bottom = cornerRadius * 2,
                ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Right edge to bottom-right corner
        lineTo(size.width, size.height - cornerRadius)

        // Bottom-right corner
        arcTo(
            rect =
                Rect(
                    left = size.width - cornerRadius * 2,
                    top = size.height - cornerRadius * 2,
                    right = size.width,
                    bottom = size.height,
                ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Bottom edge to bottom-left corner
        lineTo(cornerRadius, size.height)

        // Bottom-left corner
        arcTo(
            rect =
                Rect(
                    left = 0f,
                    top = size.height - cornerRadius * 2,
                    right = cornerRadius * 2,
                    bottom = size.height,
                ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // Left edge to top-left corner
        lineTo(0f, cornerRadius)

        // Top-left corner
        arcTo(
            rect =
                Rect(
                    left = 0f,
                    top = 0f,
                    right = cornerRadius * 2,
                    bottom = cornerRadius * 2,
                ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        close()
    }

@Preview
@Composable
private fun GlassmorphismContainerPreview() {
    DefaultPreview {
        GlassmorphismContainer(
            modifier = Modifier.padding(16.dp),
            cornerRadius = 24.dp,
            blurRadius = 10.dp,
            backgroundColor = Color.White,
            backgroundAlpha = 0.1f,
            contentPadding = 16.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Text(
                    text = "Glassmorphism Effect",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun GlassmorphismContainerSmallPaddingPreview() {
    DefaultPreview {
        GlassmorphismContainer(
            modifier = Modifier.padding(16.dp),
            cornerRadius = 24.dp,
            blurRadius = 10.dp,
            backgroundColor = Color.White,
            backgroundAlpha = 0.1f,
            contentPadding = 8.dp, // Smaller padding
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Text(
                    text = "Small Padding (8.dp)",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun GlassmorphismContainerDarkPreview() {
    DefaultPreview(isDark = true) {
        GlassmorphismContainer(
            modifier = Modifier.padding(16.dp),
            cornerRadius = 24.dp,
            blurRadius = 10.dp,
            backgroundColor = Color.Black,
            backgroundAlpha = 0.1f,
            contentPadding = 16.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Text(
                    text = "Glassmorphism Effect (Dark)",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
