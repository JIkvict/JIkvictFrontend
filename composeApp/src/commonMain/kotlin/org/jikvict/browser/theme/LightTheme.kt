package org.jikvict.browser.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object LightTheme : CustomTheme {
    val colorScheme = lightColorScheme(
        // Primary colors
        primary = Color(0xFF3574F0),         // Blue 4 (Primary)
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD4E2FF), // Blue 11
        onPrimaryContainer = Color(0xFF2E55A3), // Blue 1
        inversePrimary = Color(0xFF88ADF7),   // Blue 8

        // Secondary colors
        secondary = Color(0xFF208A3C),       // Green 4 (Primary)
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE6F7E9), // Green 10
        onSecondaryContainer = Color(0xFF1E6B33), // Green 1

        // Tertiary colors
        tertiary = Color(0xFF834DF0),        // Purple 4 (Primary)
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFEFE5FF), // Purple 8
        onTertiaryContainer = Color(0xFF55339C), // Purple 1

        // Background colors
        background = Color(0xFFFFFFFF),      // Gray 14
        onBackground = Color(0xFF000000),    // Gray 1 (Primary)

        // Surface colors
        surface = Color(0xFFFFFFFF),         // Gray 14
        onSurface = Color(0xFF000000),       // Gray 1 (Primary)

        surfaceVariant = Color(0xFFF7F8FA),  // Gray 13
        onSurfaceVariant = Color(0xFF6C707E), // Gray 6
        surfaceTint = Color(0xFF3574F0),     // Blue 4 (Primary)

        inverseSurface = Color(0xFF27282E),  // Gray 2
        inverseOnSurface = Color(0xFFD3D5DB), // Gray 10

        // Error colors
        error = Color(0xFFDB3B4B),           // Red 4 (Primary)
        onError = Color.White,
        errorContainer = Color(0xFFFFF2F3),  // Red 10
        onErrorContainer = Color(0xFFAD2B38), // Red 1

        // Other system colors
        outline = Color(0xFFA8ADBD),         // Gray 8
        outlineVariant = Color(0xFFDFE1E5),  // Gray 11
        scrim = Color(0x66000000),           // Semi-transparent black

        // Additional surface colors for Material 3
        surfaceBright = Color(0xFFF7F8FA),   // Gray 13
        surfaceDim = Color(0xFFEBECF0),      // Gray 12
        surfaceContainer = Color(0xFFF7F8FA), // Gray 13
        surfaceContainerHigh = Color(0xFFEBECF0), // Gray 12
        surfaceContainerHighest = Color(0xFFDFE1E5), // Gray 11
        surfaceContainerLow = Color(0xFFFFFFFF), // Gray 14
        surfaceContainerLowest = Color(0xFFFFFFFF), // Gray 14
    )

    override fun colorScheme(): ColorScheme {
        return colorScheme
    }
}