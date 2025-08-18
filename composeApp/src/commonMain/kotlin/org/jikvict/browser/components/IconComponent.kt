package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.util.LocalThemeSwitcherProvider

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IconComponent(
    iconVector: ImageVector,
    hoverable: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    tint: Color = Color.Unspecified,
    enabled: Boolean = true,
    iconSize: Dp = 24.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val theme = LocalThemeSwitcherProvider.current.isDark
    val bgColor = if (theme.value) MaterialTheme.colorScheme.surfaceVariant else LightColors.Blue11

    Box(
        modifier =
            Modifier
                .size(32.dp)
                .hoverable(interactionSource, hoverable)
                .then(
                    if (onClick != null && enabled) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ).background(
                    if (isHovered && hoverable) bgColor else Color.Transparent,
                    RoundedCornerShape(8.dp),
                ).then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            iconVector,
            contentDescription = null,
            tint = tint,
            modifier =
                Modifier
                    .size(iconSize),
        )
    }
}

@Composable
fun IconComponent(
    iconPainter: Painter,
    hoverable: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    tint: Color = Color.Unspecified,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val theme = LocalThemeSwitcherProvider.current.isDark
    val bgColor = if (theme.value) MaterialTheme.colorScheme.surfaceVariant else LightColors.Blue11

    Box(
        modifier =
            Modifier
                .size(32.dp)
                .hoverable(interactionSource, hoverable)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ).background(
                    if (isHovered && hoverable) bgColor else Color.Transparent,
                    RoundedCornerShape(8.dp),
                ).then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            iconPainter,
            contentDescription = null,
            tint = tint,
            modifier =
                Modifier
                    .size(24.dp),
        )
    }
}
