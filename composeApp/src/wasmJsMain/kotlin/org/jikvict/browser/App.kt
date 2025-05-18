package org.jikvict.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jikvict.browser.icons.myiconpack.Menu
import org.jikvict.browser.icons.myiconpack.User
import org.jikvict.browser.theme.DarkTheme

@Composable
fun App() {
    MaterialTheme(
        colorScheme = DarkTheme.colorScheme
    ) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header(onMenuClick = { scope.launch { drawerState.open() } })
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
                }
            }

            Footer()
        }
    }
}

@Composable
fun Header(onMenuClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .debugBorder(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .debugBorder(Color.Blue)
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Menu, contentDescription = "Menu",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .debugBorder(Color.Red)
                )
            }
            Icon(
                imageVector = User,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .debugBorder(Color.Green),
                tint = Color.Unspecified
            )
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
