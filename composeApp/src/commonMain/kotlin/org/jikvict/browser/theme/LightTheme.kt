package org.jikvict.browser.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.jikvict.browser.constant.LightColors

object LightTheme : CustomTheme {
    val colorScheme = lightColorScheme(
        // Primary colors
        primary = LightColors.Blue5,         // Primary, more vibrant
        onPrimary = LightColors.Gray14,      // White
        primaryContainer = LightColors.Blue10, // Light blue background
        onPrimaryContainer = LightColors.Blue2, // Darker blue for text on light blue
        inversePrimary = LightColors.Blue7,   // Medium blue

        // Secondary colors
        secondary = LightColors.Blue5,       // Primary, more vibrant
        onSecondary = LightColors.Gray14,    // White
        secondaryContainer = LightColors.Blue9, // Light blue background
        onSecondaryContainer = LightColors.Blue2, // Darker blue for text on light blue

        // Tertiary colors
        tertiary = LightColors.Purple5,      // Primary, more vibrant
        onTertiary = LightColors.Gray14,     // White
        tertiaryContainer = LightColors.Purple7, // Light purple background
        onTertiaryContainer = LightColors.Purple2, // Darker purple for text on light purple

        // Background colors
        background = LightColors.Gray14,     // Main background (white)
        onBackground = LightColors.Gray2,    // Dark text for better readability

        // Surface colors
        surface = LightColors.Gray13,        // Secondary background (light gray)
        onSurface = LightColors.Gray2,       // Dark text for better readability

        surfaceVariant = LightColors.Gray12, // Lines & separators
        onSurfaceVariant = LightColors.Gray5, // Secondary text & icons
        surfaceTint = LightColors.Blue5,     // Primary, more vibrant

        inverseSurface = LightColors.Gray3,  // Dark background
        inverseOnSurface = LightColors.Gray12, // Light text on dark background

        // Error colors
        error = LightColors.Red5,            // Primary, more vibrant
        onError = LightColors.Gray14,        // White
        errorContainer = LightColors.Red9,   // Light red background
        onErrorContainer = LightColors.Red2, // Darker red for text on light red

        // Other system colors
        outline = LightColors.Gray7,         // Secondary text & icons
        outlineVariant = LightColors.Gray10, // Lines & separators
        scrim = Color(0, 0, 0, 0x66),        // Semi-transparent black

        // Additional surface colors for Material 3
        surfaceBright = LightColors.Gray14,  // Brightest surface (white)
        surfaceDim = LightColors.Gray12,     // Dimmed surface
        surfaceContainer = LightColors.Gray13, // Container surface
        surfaceContainerHigh = LightColors.Gray11, // Higher container surface
        surfaceContainerHighest = LightColors.Gray10, // Highest container surface
        surfaceContainerLow = LightColors.Gray13, // Lower container surface
        surfaceContainerLowest = LightColors.Gray14, // Lowest container surface
    )

    override fun colorScheme(): ColorScheme {
        return colorScheme
    }
}
