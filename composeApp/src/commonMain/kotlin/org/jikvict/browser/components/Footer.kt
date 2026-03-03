package org.jikvict.browser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.openExternalUrl

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Footer(modifier: Modifier = Modifier) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "JIkvict",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Text(
                text = " • Problems: ikvict07@gmail.com",
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            IconButton(
                onClick = { openExternalUrl("mailto:ikvict07@gmail.com") },
                modifier = Modifier.size(32.dp).align(Alignment.CenterVertically),
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Send email",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewFooter() {
    DefaultPreview(true) {
        Footer()
    }
}
