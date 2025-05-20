package org.jikvict.browser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.api.apis.BasicControllerApi
import org.jikvict.browser.components.IconComponent
import org.jikvict.browser.icons.MyIconPack
import org.jikvict.browser.icons.myiconpack.Code
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.icons.myiconpack.User
import org.jikvict.browser.theme.DarkTheme

@Preview
@Composable
fun App() {
    MaterialTheme(
        colorScheme = DarkTheme.colorScheme
    ) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val response = remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            try {
                val r = BasicControllerApi().hello().body()
                response.value = r.message
            } catch (e: Exception) {
                response.value = "Error: ${e.message}"
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = { DrawerContent() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .safeContentPadding()
                ) {
                    Text(response.value)
                }
            }

            Footer()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(35.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,

                ) {
                IconComponent(MyIconPack.Ijlogo)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                IconComponent(MyIconPack.Code, hoverable = true)
            }
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconComponent(MyIconPack.User)
        }
    }
}

@Composable
fun DrawerContent() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .width(360.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .fillMaxHeight()
    ) {
        Text("Menu Item 1", color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text("Menu Item 2", color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Footer",
            color = MaterialTheme.colorScheme.onSecondaryContainer,

            )
    }
}

fun Modifier.debugBorder(
    color: Color = Color.Red,
    strokeWidth: Dp = 1.dp
): Modifier = this.then(
    if (!DebugConfig.showBorders) {
        Modifier
    } else {
        Modifier.drawBehind {
            drawRect(
                color = color,
                style = Stroke(width = strokeWidth.toPx())
            )
        }
    }
)

object DebugConfig {
    var showBorders by mutableStateOf(false)
}

@Preview
@Composable
fun PreviewHeader() {
    Footer()
}