package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jikvictfrontend.composeapp.generated.resources.Res
import jikvictfrontend.composeapp.generated.resources.kotlink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.ThemeSwitcherProvider
import kotlin.reflect.KClass


@Composable
fun MakeJarScreenComposable() {
    val iconId = "icon"
    val annotatedText = buildAnnotatedString {
        append("MA")
        appendInlineContent(iconId, "[icon]")
        append("E")
    }
    val themeSwitcher = ThemeSwitcherProvider.current
    val theme by themeSwitcher.isDark

    val lightPurple = LocalAppColors.current.Purple3
    val darkPurple = LocalAppColors.current.Purple6
    val purple = mutableStateOf(if (theme) darkPurple else lightPurple)
    val lightRed = LocalAppColors.current.Red3
    val darkRed = LocalAppColors.current.Red6
    val red = mutableStateOf(if (theme) darkRed else lightRed)
    val size: TextAutoSize = TextAutoSize.StepBased(20.sp, 116.sp, 8.sp)

    DefaultScreen {
        println("Recomposing with theme: $theme")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val spacing = 12.dp
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.55f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val inlineContent = mapOf(
                    iconId to InlineTextContent(
                        Placeholder(
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                            width = 1.em,
                            height = 1.em,
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.kotlink),
                            contentDescription = null,
                            tint = purple.value,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    BasicText(
                        text = annotatedText,
                        inlineContent = inlineContent,
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        color = { purple.value },
                        autoSize = size,
                        modifier = Modifier.weight(0.5f).fillMaxSize(),
                    )
                    BasicText(
                        text = ".JAR",
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        color = { purple.value },
                        autoSize = size,
                        modifier = Modifier.weight(0.5f).fillMaxSize(),
                    )
                }

                Spacer(modifier = Modifier.height(spacing))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    BasicText(
                        text = "NOT ",
                        minLines = 1,
                        maxLines = 1,
                        color = { red.value },
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        autoSize = size,
                        modifier = Modifier.weight(0.5f)

                    )
                    BasicText(
                        text = ".WAR",
                        minLines = 1,
                        maxLines = 1,
                        color = { red.value },
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        autoSize = size,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }

    }
}

@Serializable
@SerialName("home")
class MakeJarScreen : NavigableScreen {
    override val compose: @Composable (() -> Unit)
        get() = { MakeJarScreenComposable() }
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
        MakeJarScreenComposable()
    }
}

@Preview
@Composable
fun MakeJarScreenPreviewLight() {
    DefaultPreview(false) {
        MakeJarScreenComposable()
    }
}

