package org.jikvict.browser.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import org.jikvict.browser.constant.DarkColors

object DarkTheme: CustomTheme {
    val colorScheme: ColorScheme =
        darkColorScheme(
            // Primary colors
            primary = DarkColors.Blue4,         // Darker Primary
            onPrimary = DarkColors.Gray14,      // White
            primaryContainer = DarkColors.Gray1, // Main background (editor)
            onPrimaryContainer = DarkColors.Blue10,
            inversePrimary = DarkColors.Blue4,   // Darker Primary

            // Secondary colors
            secondary = DarkColors.Purple6,     // Primary
            onSecondary = DarkColors.Gray14,    // White
            secondaryContainer = DarkColors.Gray2, // Secondary background
            onSecondaryContainer = DarkColors.Purple10,

            // Tertiary colors
            tertiary = DarkColors.Purple6,      // Primary
            onTertiary = DarkColors.Gray14,     // White
            tertiaryContainer = DarkColors.Gray3, // Lines & separators
            onTertiaryContainer = DarkColors.Purple10,

            // Background colors
            background = Color.Black,      // Main background (editor)
            onBackground = DarkColors.Gray12,   // Primary text

            // Surface colors
            surface = DarkColors.Gray2,         // Secondary background
            onSurface = DarkColors.Gray12,      // Primary text

            surfaceVariant = DarkColors.Gray3,  // Lines & separators
            onSurfaceVariant = DarkColors.Gray9, // Current line number
            surfaceTint = DarkColors.Blue4,     // Darker Primary

            inverseSurface = DarkColors.Gray12, // Primary text
            inverseOnSurface = DarkColors.Gray2, // Secondary background

            // Error colors
            error = DarkColors.Red6,           // Primary
            onError = DarkColors.Gray14,       // White
            errorContainer = DarkColors.Red3,   
            onErrorContainer = DarkColors.Red10,

            // Other system colors
            outline = DarkColors.Gray7,         // Secondary text & icons
            outlineVariant = DarkColors.Gray3,  // Lines & separators
            scrim = Color(0, 0, 0, 0x80),       // Semi-transparent black

            // Additional surface colors for Material 3
            surfaceBright = DarkColors.Gray3,   // Lines & separators
            surfaceDim = DarkColors.Gray1,      // Main background (editor)
            surfaceContainer = DarkColors.Gray2, // Secondary background
            surfaceContainerHigh = DarkColors.Gray3, // Lines & separators
            surfaceContainerHighest = DarkColors.Gray4, // General icons: Fill
            surfaceContainerLow = DarkColors.Gray2, // Secondary background
            surfaceContainerLowest = DarkColors.Gray1, // Main background (editor)
        )

    override fun colorScheme(): ColorScheme {
        return colorScheme
    }
}
