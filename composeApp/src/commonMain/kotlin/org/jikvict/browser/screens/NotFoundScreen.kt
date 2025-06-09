package org.jikvict.browser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.annotation.Register
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.util.DefaultPreview
import kotlin.reflect.KClass


@Composable
private fun NotFoundScreenComposable() {
    DefaultScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Text("Not Found Screen", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}


@Register
@Serializable
@SerialName("not-found")
class NotFoundScreen : NavigableScreen {
    override val largeScreen: @Composable (() -> Unit)
        get() = {
            NotFoundScreenComposable()
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
        NotFoundScreenComposable()
    }
}
