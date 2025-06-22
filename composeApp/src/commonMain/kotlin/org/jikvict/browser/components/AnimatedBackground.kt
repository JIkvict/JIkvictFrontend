package org.jikvict.browser.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.constant.LocalAppColors

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier.Companion,
    radiusMinSize: Float = 0.5f,
    radiusMaxSize: Float = 1.2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val themeColors = LocalAppColors.current
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(17000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y"
    )

    val radiusScale by infiniteTransition.animateFloat(
        initialValue = radiusMinSize,
        targetValue = radiusMaxSize,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )


    val colors = if (themeColors is DarkColors) {
        listOf(
            themeColors.Blue9.copy(alpha = 0.35f),
            themeColors.Purple7.copy(alpha = 0.25f),
            Color.Transparent
        )
    } else {
        themeColors as LightColors
        listOf(
            DarkColors.Blue9.copy(alpha = 0.75f),
            DarkColors.Purple7.copy(alpha = 0.65f),
            Color.Transparent
        )
    }
    Canvas(modifier = modifier.fillMaxSize().blur(150.dp)) {
        val centerX = size.width / 2f
        val centerY = -(size.height * 0.1f)

        val maxOffsetX = size.width * 0.1f
        val maxOffsetY = size.height * 0.1f

        val currentOffsetX = centerX + (offsetX / 500f - 0.5f) * maxOffsetX * 2f
        val currentOffsetY = centerY + (offsetY / 500f - 0.5f) * maxOffsetY * 2f

        val baseRadius = size.width * 0.9f

        val animatedRadius = baseRadius * radiusScale
        val moveUp = (size.height - animatedRadius).coerceAtMost(size.height / 2)

        drawRect(
            brush = Brush.Companion.radialGradient(
                colors = colors,
                center = Offset(currentOffsetX, currentOffsetY + moveUp),
                radius = animatedRadius,
                tileMode = TileMode.Companion.Clamp
            ),
            size = size,
        )
    }
}