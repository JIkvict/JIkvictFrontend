@file:Suppress("unused")

package org.jikvict.browser.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastFilter
import org.jikvict.browser.components.SuggestedFontSizesStatus.Companion.validSuggestedFontSizes
import org.jikvict.browser.util.dpSizeRoundToIntSize
import org.jikvict.browser.util.intPxToSp
import org.jikvict.browser.util.spRoundToPx
import org.jikvict.browser.util.spToIntPx
import kotlin.math.min

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = emptyList(),
    suggestedFontSizesStatus: SuggestedFontSizesStatus = SuggestedFontSizesStatus.UNKNOWN,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    alignment: Alignment = Alignment.TopStart,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    lineSpaceRatio: Float = 1.2f,
) {
    AutoSizeText(
        text = AnnotatedString(text),
        modifier = modifier,
        color = color,
        suggestedFontSizes = suggestedFontSizes,
        suggestedFontSizesStatus = suggestedFontSizesStatus,
        stepGranularityTextSize = stepGranularityTextSize,
        minTextSize = minTextSize,
        maxTextSize = maxTextSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        alignment = alignment,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
        lineSpacingRatio = lineSpaceRatio,
    )
}

@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = emptyList(),
    suggestedFontSizesStatus: SuggestedFontSizesStatus = SuggestedFontSizesStatus.UNKNOWN,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    alignment: Alignment = Alignment.TopStart,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    lineSpacingRatio: Float = 1.2f,
) {
    val newDensity = Density(density = LocalDensity.current.density, fontScale = 1F)
    CompositionLocalProvider(LocalDensity provides newDensity) {
        BoxWithConstraints(
            modifier = modifier,
            contentAlignment = alignment,
        ) {
            val combinedTextStyle =
                LocalTextStyle.current +
                        style.copy(
                            color = color.takeIf { it.isSpecified } ?: style.color,
                            fontStyle = fontStyle ?: style.fontStyle,
                            fontWeight = fontWeight ?: style.fontWeight,
                            fontFamily = fontFamily ?: style.fontFamily,
                            letterSpacing = letterSpacing.takeIf { it.isSpecified } ?: style.letterSpacing,
                            textDecoration = textDecoration ?: style.textDecoration,
                            textAlign =
                                if (style.textAlign == TextAlign.Justify) {
                                    TextAlign.Justify
                                } else {
                                    when (alignment) {
                                        Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> TextAlign.Start
                                        Alignment.TopCenter, Alignment.Center, Alignment.BottomCenter -> TextAlign.Center
                                        Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> TextAlign.End
                                        else -> TextAlign.Unspecified
                                    }
                                },
                        )

            val layoutDirection = LocalLayoutDirection.current
            val density = LocalDensity.current
            val fontFamilyResolver = LocalFontFamilyResolver.current
            val textMeasurer = rememberTextMeasurer()

            val coercedLineSpacingRatio = lineSpacingRatio.takeIf { it.isFinite() && it >= 1f } ?: 1f

            val shouldMoveBackward: (TextUnit) -> Boolean = { size ->
                val safeFontSize = if (size.isSpecified && size.value > 0f) size else 1.sp
                val safeLineHeight = safeFontSize * coercedLineSpacingRatio
                shouldShrink(
                    text = text,
                    textStyle =
                        combinedTextStyle.copy(
                            fontSize = safeFontSize,
                            lineHeight = safeLineHeight,
                        ),
                    maxLines = maxLines,
                    layoutDirection = layoutDirection,
                    softWrap = softWrap,
                    density = density,
                    fontFamilyResolver = fontFamilyResolver,
                    textMeasurer = textMeasurer,
                )
            }

            val electedFontSize =
                remember(
                    key1 = suggestedFontSizes,
                    key2 = suggestedFontSizesStatus,
                ) {
                    if (suggestedFontSizesStatus == SuggestedFontSizesStatus.VALID) {
                        suggestedFontSizes
                    } else {
                        suggestedFontSizes.validSuggestedFontSizes
                    }
                }?.let {
                    remember(
                        key1 = it,
                        key2 = shouldMoveBackward,
                    ) {
                        it.findElectedValue(shouldMoveBackward = shouldMoveBackward)
                    }
                } ?: run {
                    val candidateFontSizesIntProgress =
                        rememberCandidateFontSizesIntProgress(
                            density = density,
                            containerDpSize = DpSize(maxWidth, maxHeight),
                            maxTextSize = maxTextSize,
                            minTextSize = minTextSize,
                            stepGranularityTextSize = stepGranularityTextSize,
                        )
                    remember(
                        key1 = candidateFontSizesIntProgress,
                        key2 = shouldMoveBackward,
                    ) {
                        candidateFontSizesIntProgress.findElectedValue(
                            transform = { density.intPxToSp(it) },
                            shouldMoveBackward = shouldMoveBackward,
                        )
                    }
                }

            val finalFontSize = if (electedFontSize.isSpecified && electedFontSize.value > 0f) electedFontSize else 1.sp
            val finalLineHeight = finalFontSize * coercedLineSpacingRatio

            Text(
                text = text,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                minLines = minLines,
                inlineContent = inlineContent,
                onTextLayout = onTextLayout,
                style =
                    combinedTextStyle.copy(
                        fontSize = finalFontSize,
                        lineHeight = finalLineHeight,
                    ),
            )
        }
    }

}

private fun BoxWithConstraintsScope.shouldShrink(
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    layoutDirection: LayoutDirection,
    softWrap: Boolean,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    textMeasurer: TextMeasurer,
): Boolean =
    textMeasurer
        .measure(
            text = text,
            style = textStyle,
            overflow = TextOverflow.Clip,
            softWrap = softWrap,
            maxLines = maxLines,
            constraints = constraints,
            layoutDirection = layoutDirection,
            density = density,
            fontFamilyResolver = fontFamilyResolver,
        ).hasVisualOverflow

@Stable
@Composable
private fun rememberCandidateFontSizesIntProgress(
    density: Density,
    containerDpSize: DpSize,
    minTextSize: TextUnit = TextUnit.Unspecified,
    maxTextSize: TextUnit = TextUnit.Unspecified,
    stepGranularityTextSize: TextUnit = TextUnit.Unspecified,
): IntProgression {
    val max =
        remember(key1 = density, key2 = maxTextSize, key3 = containerDpSize) {
            val intSize = density.dpSizeRoundToIntSize(containerDpSize)
            min(intSize.width, intSize.height).let { maxBound ->
                maxTextSize
                    .takeIf { it.isSp }
                    ?.let { density.spRoundToPx(it) }
                    ?.coerceIn(range = 0..maxBound)
                    ?: maxBound
            }
        }

    val min =
        remember(key1 = density, key2 = minTextSize, key3 = max) {
            minTextSize
                .takeIf { it.isSp }
                ?.let { density.spToIntPx(it) }
                ?.coerceIn(range = 0..max)
                ?: 0
        }

    val step =
        remember(
            key1 =
                listOf(
                    density,
                    min,
                    max,
                    stepGranularityTextSize,
                ),
        ) {
            stepGranularityTextSize
                .takeIf { it.isSp }
                ?.let { density.spToIntPx(it) }
                ?.coerceIn(1, max - min)
                ?: 1
        }

    return remember(key1 = min, key2 = max, key3 = step) {
        min..max step step
    }
}

internal fun <T> List<T>.findElectedValue(shouldMoveBackward: (T) -> Boolean): T =
    run {
        indices.findElectedValue(
            transform = { this[it] },
            shouldMoveBackward = shouldMoveBackward,
        )
    }

private fun <T> IntProgression.findElectedValue(
    transform: (Int) -> T,
    shouldMoveBackward: (T) -> Boolean,
): T =
    run {
        var low = first / step
        var high = last / step
        while (low <= high) {
            val mid = low + (high - low) / 2
            if (shouldMoveBackward(transform(mid * step))) {
                high = mid - 1
            } else {
                low = mid + 1
            }
        }
        transform((high * step).coerceAtLeast(first * step))
    }

enum class SuggestedFontSizesStatus {
    VALID,
    INVALID,
    UNKNOWN,
    ;

    companion object {
        val List<TextUnit>.suggestedFontSizesStatus: SuggestedFontSizesStatus
            get() =
                if (isNotEmpty() && fastAll { it.isSp } && sortedBy { it.value } == this) {
                    VALID
                } else {
                    INVALID
                }

        val List<TextUnit>.validSuggestedFontSizes: List<TextUnit>?
            get() =
                takeIf { it.isNotEmpty() }
                    ?.fastFilter { it.isSp }
                    ?.takeIf { it.isNotEmpty() }
                    ?.sortedBy { it.value }
    }
}