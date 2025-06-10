package org.jikvict.browser.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

/**
 * A reusable component for Lottie animations that reduces boilerplate code.
 *
 * @param animationPath The path to the Lottie animation JSON file
 * @param hoverable Whether the icon should show hover effects
 * @param modifier Modifier for the icon
 * @param onClick Callback for when the icon is clicked
 * @param tint Color to tint the icon
 * @param initialProgress Initial progress of the animation (0f to 1f)
 * @param animationType The type of animation to play when clicked
 */
@Composable
fun AnimatedIconComponent(
    animationPath: String,
    hoverable: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onEnd: (() -> Unit)? = null,
    tint: Color = Color.Unspecified,
    initialProgress: Float = 0f,
    animationType: AnimationType = AnimationType.ONCE_FORWARD_THEN_BACKWARD,
    speed: Float = 1f
) {
    val animatedIcon by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            jikvictfrontend.composeapp.generated.resources.Res.readBytes(animationPath).decodeToString()
        )
    }

    var isAnimating by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(initialProgress) }
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            when (animationType) {
                AnimationType.ONCE_FORWARD -> {
                    // Animate from current progress to 1
                    val startProgress = progress
                    val targetProgress = 1f

                    val steps = 30
                    val stepDuration = (16L / speed).roundToLong()
                    val stepSize = (targetProgress - startProgress) / steps

                    repeat(steps) { step ->
                        progress = startProgress + stepSize * (step + 1)
                        delay(stepDuration)
                    }

                    progress = targetProgress

                    // Reset progress to 0 after a short delay to allow the final frame to be visible
                    delay(100)
                    progress = 0f
                }

                AnimationType.ONCE_BACKWARD -> {
                    // Animate from current progress to 0
                    val startProgress = progress
                    val targetProgress = 0f

                    val steps = 30
                    val stepDuration = (16L / speed).roundToLong()
                    val stepSize = (targetProgress - startProgress) / steps

                    repeat(steps) { step ->
                        progress = startProgress + stepSize * (step + 1)
                        delay(stepDuration)
                    }

                    progress = targetProgress
                }

                AnimationType.TOGGLE -> {
                    // Toggle between 0 and 1
                    val targetProgress = if (progress < 0.5f) 1f else 0f
                    val startProgress = progress

                    val steps = 30
                    val stepDuration = (16L / speed).roundToLong()
                    val stepSize = (targetProgress - startProgress) / steps

                    repeat(steps) { step ->
                        progress = startProgress + stepSize * (step + 1)
                        delay(stepDuration)
                    }

                    progress = targetProgress
                }

                AnimationType.ONCE_FORWARD_THEN_BACKWARD -> {
                    // Animate from current progress to 1, then back to 0
                    val steps = 30
                    val stepDuration = (16L / speed).roundToLong()

                    // First animate to 1
                    repeat(steps) { step ->
                        progress = step / (steps - 1f)
                        delay(stepDuration)
                    }

                    progress = 1f

                    // Then animate back to 0
                    repeat(steps) { step ->
                        progress = 1f - (step / (steps - 1f))
                        delay(stepDuration)
                    }

                    progress = 0f
                }
            }

            isAnimating = false

            onEnd?.invoke()
        }
    }

    IconComponent(
        iconPainter = rememberLottiePainter(
            composition = animatedIcon,
            progress = { progress }
        ),
        hoverable = hoverable,
        modifier = modifier,
        tint = tint,
        onClick = {
            if (!isAnimating) {
                isAnimating = true
                if (onClick != null) {
                    onClick()
                }
            }
        }
    )
}
/**
 * Enum defining different types of animations.
 */
enum class AnimationType {
    /**
     * Animate from current progress to 1 and stay there.
     */
    ONCE_FORWARD,

    /**
     * Animate from current progress to 0 and stay there.
     */
    ONCE_BACKWARD,

    /**
     * Toggle between 0 and 1 based on current progress.
     */
    TOGGLE,

    /**
     * Animate from current progress to 1, then back to 0.
     */
    ONCE_FORWARD_THEN_BACKWARD
}
