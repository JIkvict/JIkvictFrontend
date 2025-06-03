package org.jikvict.browser.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import jikvictfrontend.composeapp.generated.resources.Res
import jikvictfrontend.composeapp.generated.resources.inter
import jikvictfrontend.composeapp.generated.resources.inter_semibold
import jikvictfrontend.composeapp.generated.resources.jetbrains_mono
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@Composable
@OptIn(markerClass = [ExperimentalResourceApi::class])
actual fun rememberInterFontFamily(): FontFamily {
    val inter = Font(Res.font.inter)
    val interSemibold = Font(Res.font.inter_semibold, weight = FontWeight.SemiBold)

    val font1 = inter
    val font2 = interSemibold

    return FontFamily(font1, font2)
}

@Composable
@OptIn(markerClass = [ExperimentalResourceApi::class])
actual fun rememberJetBrainsMonoFontFamily(): FontFamily {
    val jetBrainsMono = Font(Res.font.jetbrains_mono, weight = FontWeight.Bold)
    val jetBrainsMonoBold =
        Font(Res.font.jetbrains_mono, weight = FontWeight.Bold)
    val jetBrainsMonoMedium =
        Font(Res.font.jetbrains_mono, weight = FontWeight.Medium)

    return FontFamily(
        jetBrainsMono,
        jetBrainsMonoBold,
        jetBrainsMonoMedium
    )
}