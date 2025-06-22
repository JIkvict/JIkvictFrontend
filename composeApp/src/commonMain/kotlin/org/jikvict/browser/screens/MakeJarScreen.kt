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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import org.jikvict.browser.components.AnimatedBackground
import org.jikvict.browser.components.AutoSizeText
import org.jikvict.browser.components.CustomCard
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.IconComponent
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider
import org.jikvict.browser.util.responsive.Breakpoint
import org.jikvict.browser.util.responsive.ResponsiveModifierBuilder
import org.jikvict.browser.util.responsive.ResponsiveValueBuilder
import org.jikvict.browser.util.responsive.adaptiveValue
import org.jikvict.browser.util.responsive.responsive
import org.jikvict.browser.viewmodel.MakeJarScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass


val mainColumnModifier = ResponsiveModifierBuilder {
    base {
        Modifier.wrapContentHeight()
    }
    Breakpoint.SM {
        Modifier.fillMaxWidth(0.9f)
    }
    Breakpoint.MD {
        Modifier.fillMaxWidth(0.55f)
    }
    Breakpoint.LG {
        Modifier.fillMaxWidth(0.5f)
    }
}

val minTextSize = ResponsiveValueBuilder {
    Breakpoint.SM { 20.sp }
    Breakpoint.MD { 30.sp }
    Breakpoint.LG { 60.sp }
}
val maxTextSize = ResponsiveValueBuilder {
    Breakpoint.SM { 60.sp }
    Breakpoint.MD { 60.sp }
    Breakpoint.LG { 116.sp }
}


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


    val constraints = defaultScope.boxWithConstraintsScope
    val screenWidth = constraints.maxWidth
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

    val minText = minTextSize.toValue()
    val maxText = maxTextSize.toValue()
    val radius = adaptiveValue(1f to 1.5f, 0.8f to 1.1f, 0.7f to 1f)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            val solveTestCreatePosition by viewModel.solveTestCreatePosition.collectAsState()
            AnimatedBackground(
                modifier = Modifier.height(spacerHeightDp * 2 + jarWarHeightPx.dp).fillMaxWidth().zIndex(-1f),
                radius.first,
                radius.second
            )
            Column {
                Spacer(modifier = Modifier.height(spacerHeightDp))
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier
                            .responsive(mainColumnModifier)
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
                                        softWrap = false,
                                        minTextSize = minText,
                                        maxTextSize = maxText,
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
                                    minTextSize = minText,
                                    maxTextSize = maxText,
                                    softWrap = false,
                                )
                                AutoSizeText(
                                    text = ".WAR",
                                    color = red,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        textAlign = TextAlign.Right,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    minTextSize = minText,
                                    maxTextSize = maxText,
                                    softWrap = false,
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
                                        defaultScope.verticalScroll.animateScrollTo(solveTestCreatePosition)
                                    }
                                }
                            }
                        }

                        FloatingActionButton(
                            onClick = {
                                val scope = scope
                                scope.launch {
                                    defaultScope.verticalScroll.animateScrollTo(solveTestCreatePosition)
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




        Column(
            modifier = Modifier.responsive(mainColumnModifier).onGloballyPositioned {
                viewModel.updateSolveTestCreatePosition(it.positionInParent().y.toInt())
            },
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            AutoSizeText(
                text = "Solve. Test. Create",
                style = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                softWrap = false,
                minTextSize = minText,
                maxTextSize = maxText,
            )
            Spacer(modifier = Modifier.height(16.dp))


            CustomCard(Icons.Default.Download, "Download the plugin in IntelliJ IDEA Marketplace")
            CustomCard(Icons.Default.Task, "Solve tasks and test your knowledge")
            CustomCard(Icons.Default.Create, "Create elegant solutions")
        }
        Spacer(modifier = Modifier.height(16.dp))
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
