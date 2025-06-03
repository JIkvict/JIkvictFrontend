package org.jikvict.browser.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.annotation.Register
import org.jikvict.browser.components.DefaultScreen
import org.jikvict.browser.util.DefaultPreview
import kotlin.reflect.KClass

@Composable
private fun HomeScreenComposable(
    i: Int,
) {
    DefaultScreen {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text("Parameter i: $i", color = MaterialTheme.colorScheme.onBackground)

                repeat(100) {
                    Text("Home Screen $it", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Register
@Serializable
@SerialName("home2")
data class HomeScreen(
    val i: Int,
    val b: String = "default"
) : NavigableScreen {
    override val compose: @Composable (() -> Unit)
        get() = {
            HomeScreenComposable(i)
        }
}

object HomeScreenRouterRegistrar : ScreenRouterRegistrar<HomeScreen> {
    override val screen: KClass<HomeScreen>
        get() = HomeScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen {
        return try {
            println("i is ${params["i"]}, b is ${params["b"]}")
            val i = params["i"]?.toIntOrNull() ?: 0
            val b = params["b"] ?: "default"
            println("Constructing HomeScreen with i=$i, b=$b")
            return HomeScreen(i, b)
        } catch (_: Exception) {
            NotFoundScreen()
        }
    }
}

object HomeScreenRegistrar : ScreenRegistrar<HomeScreen> by createRegistrar()

@Preview
@Composable
fun HomeScreenPreview() {
    DefaultPreview {
        HomeScreenComposable(1)
    }

}
