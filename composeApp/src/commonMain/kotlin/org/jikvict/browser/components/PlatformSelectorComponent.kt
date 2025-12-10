package org.jikvict.browser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import jikvictfrontend.composeapp.generated.resources.Res
import jikvictfrontend.composeapp.generated.resources.apple
import jikvictfrontend.composeapp.generated.resources.linux
import jikvictfrontend.composeapp.generated.resources.windows
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.util.DefaultPreview


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlatformSelectorComponent(
    modifier: Modifier = Modifier,
    selectedPlatformIndex: Int,
    onPlatformSelected: (Int) -> Unit,
    winContent: @Composable () -> Unit,
    macContent: @Composable () -> Unit,
    linuxContent: @Composable () -> Unit,
) {
    val options = listOf("Windows", "macOS", "Linux")
    val checkedIcons = listOf(Res.drawable.windows, Res.drawable.apple, Res.drawable.linux).map {
        painterResource(it)
    }
    BoxWithConstraints(modifier) {
        val showLabels = maxWidth > 400.dp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            options.forEachIndexed { index, label ->
                ToggleButton(
                    checked = selectedPlatformIndex == index,
                    onCheckedChange = { onPlatformSelected(index) },
                    modifier = Modifier.weight(1f).semantics { role = Role.RadioButton },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                ) {
                    Icon(
                        checkedIcons[index],
                        contentDescription = "Icon",
                        modifier = Modifier.size(ToggleButtonDefaults.IconSize)
                    )
                    if (showLabels) {
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                        Text(label)
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    when (selectedPlatformIndex) {
        0 -> winContent()
        1 -> macContent()
        2 -> linuxContent()
    }
}


@Preview
@Composable
fun PlatformSelectorComponentPreview() {
    DefaultPreview {
        var selectedIndex by remember { mutableStateOf(0) }
        PlatformSelectorComponent(
            selectedPlatformIndex = selectedIndex,
            onPlatformSelected = { selectedIndex = it },
            winContent = { /* Windows-specific UI */ },
            macContent = { /* macOS-specific UI */ },
            linuxContent = { /* Linux-specific UI */ }
        )
    }
}