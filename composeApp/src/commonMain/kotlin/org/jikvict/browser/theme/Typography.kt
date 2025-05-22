package org.jikvict.browser.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
@OptIn(ExperimentalResourceApi::class)
expect fun rememberInterFontFamily(): FontFamily

@Composable
@OptIn(ExperimentalResourceApi::class)
expect fun rememberJetBrainsMonoFontFamily(): FontFamily

class JIkvictTypography(inter: FontFamily, jetBrainsMono: FontFamily) {
    val typography = Typography(
        displayLarge = TextStyle(
            // H1 UI
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 24.sp,
        ),
        displayMedium = TextStyle(
            // H2 UI
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 20.sp,
        ),
        displaySmall = TextStyle(
            // Paragraph UI
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
        headlineMedium = TextStyle(
            // Default Mono
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 22.sp,
        ),
        headlineSmall = TextStyle(
            // Default Bold Mono
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 22.sp,
        ),
        titleLarge = TextStyle(
            // Small Mono
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 22.sp,
        ),
        titleMedium = TextStyle(
            // Default Semibold Mono
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 16.sp,
        ),
        titleSmall = TextStyle(
            // Small Bold Mono
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 22.sp,
        ),
        bodyLarge = TextStyle(
            // Paragraph Mono Medium
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        bodyMedium = TextStyle(
            // Paragraph Mono Medium Semibold
            fontFamily = jetBrainsMono,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        bodySmall = TextStyle(
            // Default UI
            fontFamily = inter,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 16.sp,
        )
    )
}
