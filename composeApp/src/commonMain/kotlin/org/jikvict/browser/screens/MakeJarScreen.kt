package org.jikvict.browser.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import jikvictfrontend.composeapp.generated.resources.Res
import jikvictfrontend.composeapp.generated.resources.kotlink
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.AutoSizeText
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.IconComponent
import org.jikvict.browser.components.SimpleStaggeredGrid
import org.jikvict.browser.constant.DarkColors
import org.jikvict.browser.constant.LightColors
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider
import org.jikvict.browser.util.adaptiveValue
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@OptIn(
    ExperimentalMaterial3AdaptiveApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun MakeJarScreenComposable(defaultScope: DefaultScreenScope) = with(defaultScope) {
    val viewModel = koinViewModel<MakeJarScreenViewModel>()

    val iconId = "icon"
    val annotatedText = buildAnnotatedString {
        append("MA")
        appendInlineContent(iconId, "K[")
        append("E")
    }


    val themeSwitcher = ThemeSwitcherProvider.current
    val theme by themeSwitcher.isDark
    val appColors = LocalAppColors.current

    viewModel.updateColors(theme, appColors)

    val purple by viewModel.purpleColor.collectAsState()
    val red by viewModel.redColor.collectAsState()

    val feedItems by viewModel.feedItems.collectAsState()

    val constraints = defaultScope.boxWithConstraintsScope
    val screenWidth = constraints.maxWidth
    val isLargeScreen = screenWidth >= 600.dp
    val screenHeight = constraints.maxHeight

    val density = LocalDensity.current

    val jarWarHeightPx by viewModel.jarWarHeightPx.collectAsState()

    val spacerHeightDp = remember(jarWarHeightPx, screenHeight) {
        if (jarWarHeightPx == 0) {
            0.dp
        } else {
            val totalHeightPx = with(density) { screenHeight.toPx() }
            val remainingPx = (totalHeightPx - jarWarHeightPx - defaultScope.headerHeight).coerceAtLeast(0f)
            with(density) { (remainingPx / 2f).toDp() }
        }
    }

    val columnScale = adaptiveValue(0.9f, 0.55f, 0.5f)
    val minText = adaptiveValue(20.sp, 30.sp, 60.sp)
    val maxText = adaptiveValue(60.sp, 60.sp, 116.sp)
    val radius = adaptiveValue(0.9f to 1.2f, 0.8f to 1.1f, 0.7f to 1f)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            val gridPosition by viewModel.gridPosition.collectAsState()
            AnimatedBackgroundTopLeft(
                modifier = Modifier.height(spacerHeightDp * 2 + jarWarHeightPx.dp).fillMaxWidth().zIndex(-1f),
                radius.first,
                radius.second
            )
            Column {
                Spacer(modifier = Modifier.height(spacerHeightDp))
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(columnScale)
                            .wrapContentHeight()
                            .onGloballyPositioned {
                                viewModel.updateJarWarHeightPx(it.size.height)
                                viewModel.updateJarWarOffsetY(it.positionInParent().y.toInt())
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            val inlineContent = mapOf(
                                iconId to InlineTextContent(
                                    Placeholder(
                                        width = 1.em,
                                        height = 1.em,
                                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.kotlink),
                                        contentDescription = null,
                                        tint = purple,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    AutoSizeText(
                                        text = annotatedText,
                                        inlineContent = inlineContent,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            textAlign = TextAlign.Left,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = purple,
                                        modifier = Modifier,
                                        softWrap = false,
                                        maxTextSize = maxText,
                                        minTextSize = minText,
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    AutoSizeText(
                                        text = ".JAR",
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            textAlign = TextAlign.Right,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = purple,
                                        modifier = Modifier.fillMaxWidth(),
                                        minTextSize = minText,
                                        maxTextSize = maxText,
                                        softWrap = false,
                                        alignment = Alignment.CenterEnd,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                AutoSizeText(
                                    text = "NOT",
                                    color = red,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        textAlign = TextAlign.Left,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    modifier = Modifier,
                                    minTextSize = minText,
                                    maxTextSize = maxText,
                                    softWrap = false,
                                    alignment = Alignment.CenterStart,
                                )
                                AutoSizeText(
                                    text = ".WAR",
                                    color = red,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        textAlign = TextAlign.Right,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    modifier = Modifier,
                                    minTextSize = minText,
                                    maxTextSize = maxText,
                                    softWrap = false,
                                    alignment = Alignment.CenterEnd,
                                )
                            }
                        }
                    }
                    val scope = rememberCoroutineScope()
                    val hoverJob = remember { mutableStateOf<Job?>(null) }
                    val animationJob = remember { mutableStateOf<Job?>(null) }
                    if (defaultScope.verticalScroll.value < (spacerHeightDp / 2).value) {
                        val scrollDownAnimation by rememberLottieComposition {
                            LottieCompositionSpec.JsonString(
                                Res.readBytes("files/scroll-down.json").decodeToString()
                            )
                        }

                        val interactionSource = remember { MutableInteractionSource() }
                        val isHovered by interactionSource.collectIsHoveredAsState()
                        val animationProgress by viewModel.animationProgress.collectAsState()

                        val coroutineScope = rememberCoroutineScope()
                        LaunchedEffect(isHovered) {
                            if (isHovered) {
                                animationJob.value?.cancel()
                                animationJob.value = coroutineScope.launch { viewModel.animateHover(isHovered) }
                                animationJob.value?.join()
                                hoverJob.value?.cancel()
                                hoverJob.value = launch {
                                    scope.launch {
                                        defaultScope.verticalScroll.animateScrollTo(gridPosition)
                                    }
                                }
                            }
                        }

                        FloatingActionButton(
                            onClick = {
                                val scope = scope
                                scope.launch {
                                    defaultScope.verticalScroll.animateScrollTo(gridPosition)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = spacerHeightDp / 2)
                                .hoverable(interactionSource, true),
                        ) {
                            IconComponent(
                                rememberLottiePainter(
                                    composition = scrollDownAnimation,
                                    progress = { animationProgress }
                                ),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        hoverJob.value?.cancel()
                        animationJob.value?.cancel()
                        viewModel.resetAnimationProgress()
                    }
                }
            }
        }


        SimpleStaggeredGrid(
            columns = if (isLargeScreen) 2 else 1,
            modifier = Modifier.padding(16.dp).fillMaxWidth(0.65f).onGloballyPositioned {
                viewModel.updateGridPosition(it.positionInParent().y.toInt())
            },
            verticalSpacing = 24,
            horizontalSpacing = 24
        ) {
            feedItems.forEach {
                FeedCard(it)
            }
        }
    }
}


@Composable
fun FeedCard(item: FeedItem) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val animatedElevation by animateDpAsState(
        targetValue = if (isHovered) 12.dp else 6.dp,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(item.height.dp)
            .scale(animatedScale)
            .hoverable(interactionSource),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = animatedElevation
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "${item.title} (Glassmorphism)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


data class FeedItem(
    val title: String,
    val description: String,
    val height: Int = 150
)

@Composable
fun AnimatedBackgroundTopLeft(
    modifier: Modifier = Modifier,
    radiusMinSize: Float = 0.5f,
    radiusMaxSize: Float = 1.2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val themeColors = LocalAppColors.current
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(17000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y"
    )

    val radiusScale by infiniteTransition.animateFloat(
        initialValue = radiusMinSize,
        targetValue = radiusMaxSize,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )


    val colors = if (themeColors is DarkColors) {
        listOf(
            themeColors.Blue9.copy(alpha = 0.35f),
            themeColors.Purple7.copy(alpha = 0.25f),
            Color.Transparent
        )
    } else {
        themeColors as LightColors
        listOf(
            DarkColors.Blue9.copy(alpha = 0.75f),
            DarkColors.Purple7.copy(alpha = 0.65f),
            Color.Transparent
        )
    }
    Canvas(modifier = modifier.fillMaxSize().blur(150.dp)) {
        val centerX = size.width / 2f
        val centerY = -(size.height * 0.1f)

        val maxOffsetX = size.width * 0.1f
        val maxOffsetY = size.height * 0.1f

        val currentOffsetX = centerX + (offsetX / 500f - 0.5f) * maxOffsetX * 2f
        val currentOffsetY = centerY + (offsetY / 500f - 0.5f) * maxOffsetY * 2f

        val baseRadius = size.width * 0.9f

        val animatedRadius = baseRadius * radiusScale
        val moveUp =  (size.height - animatedRadius).coerceAtMost(size.height / 2)

        drawRect(
            brush = Brush.radialGradient(
                colors = colors,
                center = Offset(currentOffsetX, currentOffsetY + moveUp),
                radius = animatedRadius,
                tileMode = TileMode.Clamp
            ),
            size = size,
        )
    }
}


@Serializable
@SerialName("home")
class MakeJarScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { MakeJarScreenComposable(it) }
}

object MakeJarScreenRouterRegistrar : ScreenRouterRegistrar<MakeJarScreen> {
    override val screen: KClass<MakeJarScreen>
        get() = MakeJarScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return MakeJarScreen()
    }
}

object MakeJarScreenRegistrar : ScreenRegistrar<MakeJarScreen> by createRegistrar()

@Preview
@Composable
fun MakeJarScreenPreview() {
    DefaultPreview {
        MakeJarScreenComposable(it)
    }
}

@Preview
@Composable
fun MakeJarScreenPreviewLight() {
    DefaultPreview(false) {
        MakeJarScreenComposable(it)
    }
}
