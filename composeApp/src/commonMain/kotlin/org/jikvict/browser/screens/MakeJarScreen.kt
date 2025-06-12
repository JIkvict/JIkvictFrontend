package org.jikvict.browser.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
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
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.GlassmorphismContainer
import org.jikvict.browser.components.IconComponent
import org.jikvict.browser.components.SimpleStaggeredGrid
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun MakeJarScreenComposable(defaultScope: DefaultScreenScope) {
    val viewModel = koinViewModel<MakeJarScreenViewModel>()

    val iconId = "icon"
    val annotatedText = buildAnnotatedString {
        append("MA")
        appendInlineContent(iconId, "[icon]")
        append("E")
    }

    val themeSwitcher = ThemeSwitcherProvider.current
    val theme by themeSwitcher.isDark
    val appColors = LocalAppColors.current

    viewModel.updateColors(theme, appColors)

    val purple by viewModel.purpleColor.collectAsState()
    val red by viewModel.redColor.collectAsState()
    val size: TextAutoSize = TextAutoSize.StepBased(20.sp, 116.sp, 8.sp)

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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val gridPosition by viewModel.gridPosition.collectAsState()
        Spacer(modifier = Modifier.height(spacerHeightDp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
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
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        BasicText(
                            text = annotatedText,
                            inlineContent = inlineContent,
                            minLines = 1,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.Left,
                                fontWeight = FontWeight.Bold
                            ),
                            color = { purple },
                            autoSize = if (isLargeScreen) size else null,
                            modifier = Modifier.weight(0.5f)
                        )
                        BasicText(
                            text = ".JAR",
                            minLines = 1,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.Right,
                                fontWeight = FontWeight.Bold
                            ),
                            color = { purple },
                            autoSize = if (isLargeScreen) size else null,
                            modifier = Modifier.weight(0.5f)
                        )
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
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        BasicText(
                            text = "NOT",
                            minLines = 1,
                            maxLines = 1,
                            color = { red },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.Left,
                                fontWeight = FontWeight.Bold
                            ),
                            autoSize = if (isLargeScreen) size else null,
                            modifier = Modifier.weight(0.5f)
                        )
                        BasicText(
                            text = ".WAR",
                            minLines = 1,
                            maxLines = 1,
                            color = { red },
                            style = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.Right,
                                fontWeight = FontWeight.Bold
                            ),
                            autoSize = if (isLargeScreen) size else null,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }
            val scope = rememberCoroutineScope()
            if (defaultScope.verticalScroll.value < (spacerHeightDp / 2).value) {
                val scrollDownAnimation by rememberLottieComposition {
                    LottieCompositionSpec.JsonString(
                        Res.readBytes("files/scroll-down.json").decodeToString()
                    )
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
                val animationProgress by viewModel.animationProgress.collectAsState()

                val hoverJob = remember { mutableStateOf<Job?>(null) }
                val animationJob = remember { mutableStateOf<Job?>(null) }
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
            }
        }

        Spacer(modifier = Modifier.height(spacerHeightDp))

        SimpleStaggeredGrid(
            columns = if (isLargeScreen) 2 else 1,
            modifier = Modifier.padding(16.dp).fillMaxWidth(0.65f).onGloballyPositioned {
                viewModel.updateGridPosition(it.positionInParent().y.toInt())
            },
            verticalSpacing = 10,
            horizontalSpacing = 10
        ) {
            feedItems.forEach {
                FeedCard(it)
            }
        }
    }
}


@Composable
fun FeedCard(item: FeedItem) {
    GlassmorphismExample(item)
}

@Composable
fun GlassmorphismExample(item: FeedItem) {
    GlassmorphismContainer(
        modifier = Modifier
            .fillMaxWidth()
            .height(item.height.dp),
        cornerRadius = 24.dp,
        blurRadius = 10.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        backgroundAlpha = 0.1f,
        noiseAlpha = 0.01f
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "${item.title} (Glassmorphism)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

data class FeedItem(
    val title: String,
    val description: String,
    val height: Int = 150
)

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
