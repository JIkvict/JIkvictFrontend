package org.jikvict.browser.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import jikvictfrontend.composeapp.generated.resources.Res
import jikvictfrontend.composeapp.generated.resources.inter
import jikvictfrontend.composeapp.generated.resources.inter_semibold
import jikvictfrontend.composeapp.generated.resources.jetbrains_mono
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun rememberInterFontFamily(): FontFamily {
    val inter = preloadFont(Res.font.inter)
    val interSemibold = preloadFont(Res.font.inter_semibold, weight = FontWeight.SemiBold)

    val font1 = inter.value
    val font2 = interSemibold.value

    return if (font1 != null && font2 != null) {
        FontFamily(font1, font2)
    } else {
        FontFamily.Default
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun rememberJetBrainsMonoFontFamily(): FontFamily {
    val jetBrainsMono = preloadFont(Res.font.jetbrains_mono, weight = FontWeight.Bold)
    val jetBrainsMonoBold =
        preloadFont(Res.font.jetbrains_mono, weight = FontWeight.Bold)
    val jetBrainsMonoMedium =
        preloadFont(Res.font.jetbrains_mono, weight = FontWeight.Medium)

    return if (jetBrainsMono.value != null && jetBrainsMonoBold.value != null && jetBrainsMonoMedium.value != null) {
        FontFamily(
            jetBrainsMono.value!!,
            jetBrainsMonoBold.value!!,
            jetBrainsMonoMedium.value!!,
        )
    } else {
        FontFamily.Default
    }
}
