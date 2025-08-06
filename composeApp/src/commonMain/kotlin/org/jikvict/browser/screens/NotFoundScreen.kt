package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun NotFoundScreenComposable(defaultScreenScope: DefaultScreenScope) =
    with(defaultScreenScope) {
        val animation by rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/404-animation.json").decodeToString(),
            )
        }
        Box(
            modifier = Modifier.fitContentToScreen(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                rememberLottiePainter(animation, iterations = Int.MAX_VALUE),
                contentDescription = "Not Found Animation",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxSize(0.75f),
            )
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

    override fun constructScreen(params: Map<String, String?>): NavigableScreen = NotFoundScreen()
}

object NotFoundScreenRegistrar : ScreenRegistrar<NotFoundScreen> by createRegistrar()

@Preview
@Composable
private fun NotFoundScreenPreview() {
    DefaultPreview {
        NotFoundScreenComposable(it)
    }
}
