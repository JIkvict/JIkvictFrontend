package org.jikvict.browser.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.jikvict.browser.constant.LightColors

object LightTheme : CustomTheme {
    val colorScheme = lightColorScheme(
        // Primary colors
        primary = LightColors.Blue4,         // Blue 4 (Primary)
        onPrimary = LightColors.Gray14,      // White
        primaryContainer = LightColors.Blue11, // Blue 11
        onPrimaryContainer = LightColors.Blue1, // Blue 1
        inversePrimary = LightColors.Blue8,   // Blue 8

        // Secondary colors
        secondary = LightColors.Blue4,        // Blue 4 (Primary)
        onSecondary = LightColors.Gray14,     // White
        secondaryContainer = LightColors.Blue10, // Blue 10
        onSecondaryContainer = LightColors.Blue1, // Blue 1

        // Tertiary colors
        tertiary = LightColors.Purple4,        // Purple 4 (Primary)
        onTertiary = LightColors.Gray14,       // White
        tertiaryContainer = LightColors.Purple8, // Purple 8
        onTertiaryContainer = LightColors.Purple1, // Purple 1

        // Background colors
        background = LightColors.Gray14,      // Gray 14
        onBackground = LightColors.Gray1,     // Gray 1 (Primary)

        // Surface colors
        surface = LightColors.Gray14,         // Gray 14
        onSurface = LightColors.Gray1,        // Gray 1 (Primary)

        surfaceVariant = LightColors.Gray13,  // Gray 13
        onSurfaceVariant = LightColors.Gray6, // Gray 6
        surfaceTint = LightColors.Blue4,      // Blue 4 (Primary)

        inverseSurface = LightColors.Gray2,   // Gray 2
        inverseOnSurface = LightColors.Gray10, // Gray 10

        // Error colors
        error = LightColors.Red4,             // Red 4 (Primary)
        onError = LightColors.Gray14,         // White
        errorContainer = LightColors.Red10,   // Red 10
        onErrorContainer = LightColors.Red1,  // Red 1

        // Other system colors
        outline = LightColors.Gray8,          // Gray 8
        outlineVariant = LightColors.Gray11,  // Gray 11
        scrim = Color(0, 0, 0, 0x66),         // Semi-transparent black

        // Additional surface colors for Material 3
        surfaceBright = LightColors.Gray13,   // Gray 13
        surfaceDim = LightColors.Gray12,      // Gray 12
        surfaceContainer = LightColors.Gray13, // Gray 13
        surfaceContainerHigh = LightColors.Gray12, // Gray 12
        surfaceContainerHighest = LightColors.Gray11, // Gray 11
        surfaceContainerLow = LightColors.Gray14, // Gray 14
        surfaceContainerLowest = LightColors.Gray14, // Gray 14
    )

    override fun colorScheme(): ColorScheme {
        return colorScheme
    }
}
