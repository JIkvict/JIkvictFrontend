package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import jikvictfrontend.composeapp.generated.resources.Res
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.annotation.Register
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.util.DefaultPreview
import kotlin.reflect.KClass


@Composable
fun NotFoundScreenComposable(defaultScreenScope: DefaultScreenScope) {
    val animation by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/404-animation.json").decodeToString()
        )
    }
    val density = LocalDensity.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val iconSize = remember { mutableStateOf(0.dp) }
                    Spacer(modifier = Modifier.height(defaultScreenScope.boxWithConstraintsScope.maxHeight.also { println(it) } - iconSize.value.also { println(it) }))
                    Icon(
                        rememberLottiePainter(animation, iterations = Int.MAX_VALUE),
                        contentDescription = "Not Found Animation",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxSize().onGloballyPositioned{
                            iconSize.value = it.size.height.dp
                        }
                    )
                }
            }
        }
    }
}


@Register
@Serializable
@SerialName("not-found")
class NotFoundScreen : NavigableScreen {
    @Transient
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit) = {
        NotFoundScreenComposable(it)
    }

}

object NotFoundScreenRouterRegistrar : ScreenRouterRegistrar<NotFoundScreen> {
    override val screen: KClass<NotFoundScreen>
        get() = NotFoundScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return NotFoundScreen()
    }
}

object NotFoundScreenRegistrar : ScreenRegistrar<NotFoundScreen> by createRegistrar()

@Preview
@Composable
fun NotFoundScreenPreview() {
    DefaultPreview {
        NotFoundScreenComposable(it)
    }
}
