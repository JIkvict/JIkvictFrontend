package org.jikvict.browser.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

object DarkTheme {
    val colorScheme: ColorScheme =
        ColorScheme(
            primary = Color(53, 116, 240),
            onPrimary = Color.White,
            primaryContainer = Color(30, 31, 34),
            onPrimaryContainer = Color(199, 210, 255),
            inversePrimary = Color(53, 116, 240),
            secondary = Color(160, 160, 180),
            onSecondary = Color.Black,
            secondaryContainer = Color(43, 45, 48),
            onSecondaryContainer = Color(200, 200, 220),
            tertiary = Color(135, 160, 200),
            onTertiary = Color.Black,
            tertiaryContainer = Color(50, 60, 80),
            onTertiaryContainer = Color(190, 210, 240),
            background = Color(18, 18, 18),
            onBackground = Color(230, 230, 230),
            surface = Color(20, 20, 20),
            onSurface = Color(230, 230, 230),
            surfaceVariant = Color(60, 60, 70),
            onSurfaceVariant = Color(180, 180, 200),
            surfaceTint = Color(53, 116, 240),
            inverseSurface = Color(230, 230, 230),
            inverseOnSurface = Color(20, 20, 20),
            error = Color(207, 102, 121),
            onError = Color.Black,
            errorContainer = Color(140, 40, 50),
            onErrorContainer = Color(255, 218, 220),
            outline = Color(110, 110, 130),
            outlineVariant = Color(60, 60, 70),
            scrim = Color(0, 0, 0, 0x80),
            surfaceBright = Color(30, 30, 30),
            surfaceDim = Color(12, 12, 12),
            surfaceContainer = Color(24, 24, 24),
            surfaceContainerHigh = Color(28, 28, 28),
            surfaceContainerHighest = Color(32, 32, 32),
            surfaceContainerLow = Color(20, 20, 20),
            surfaceContainerLowest = Color(10, 10, 10)
        )
}
