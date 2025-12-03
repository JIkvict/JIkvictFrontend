package org.jikvict.browser.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

/**
 * A lightweight, focus-safe searchable dropdown component.
 *
 * It intentionally avoids tying to ExposedDropdownMenuBoxScope to keep
 * Compose Multiplatform compatibility while providing a similar API structure.
 */
@Composable
fun <T> SearchableDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    searchContent: @Composable () -> Unit,
    displayContent: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    noItemsContent: @Composable () -> Unit = {
        Text(
            "No items",
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    },
) {
    androidx.compose.foundation.layout.Box {
        // The display composable is expected to act as the anchor (e.g., an OutlinedTextField)
        displayContent(modifier)

        // The dropdown popup itself. We keep it non-focusable so typing in the anchor doesn't lose focus.
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            properties = PopupProperties(focusable = false)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                searchContent()
                if (items.isEmpty()) {
                    noItemsContent()
                } else {
                    items.take(5).forEach { item ->
                        itemContent(item)
                    }
                }
            }
        }
    }
}
