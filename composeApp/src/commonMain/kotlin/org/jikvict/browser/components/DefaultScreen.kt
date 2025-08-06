package org.jikvict.browser.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.util.DefaultPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScreen(content: @Composable ColumnScope.(DefaultScreenScope) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val boxWithConstraintsScope = this@BoxWithConstraints
        val verticalScroll = rememberScrollState()
        val headerHeight = remember { mutableStateOf(0) }
        val footerHeight = remember { mutableStateOf(0) }
        val density = LocalDensity.current

        val screenHeight =
            derivedStateOf {
                with(density) {
                    (maxHeight.toPx() - headerHeight.value - footerHeight.value - 32).toDp()
                }
            }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScroll)
                    .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Header(
                modifier =
                    Modifier.onGloballyPositioned {
                        headerHeight.value = it.size.height
                    },
            )

            content(
                DefaultScreenScope(
                    boxWithConstraintsScope,
                    this,
                    verticalScroll,
                    headerHeight.value,
                    footerHeight.value,
                    screenHeight = screenHeight.value,
                ),
            )

            Footer(
                modifier =
                    Modifier.onGloballyPositioned {
                        footerHeight.value = it.size.height
                    },
            )
        }
    }
}

data class DefaultScreenScope(
    val boxWithConstraintsScope: BoxWithConstraintsScope,
    val columnScope: ColumnScope,
    val verticalScroll: ScrollState,
    val headerHeight: Int,
    val footerHeight: Int,
    val screenWidth: Dp = boxWithConstraintsScope.maxWidth,
    val screenHeight: Dp,
) {
    fun Modifier.fitContentToScreen(): Modifier = this.fillMaxWidth().height(screenHeight)
}

@Preview
@Composable
private fun DefaultScreenPreview() {
    DefaultPreview {
        DefaultScreen {
        }
    }
}
