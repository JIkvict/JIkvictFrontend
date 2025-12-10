package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.LocalThemeSwitcherProvider

enum class AlertType {
    Note, Tip, Important, Warning, Caution
}

@Composable
fun Alert(
    type: AlertType,
    content: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    val themeSwitcher = LocalThemeSwitcherProvider.current
    val isDark = themeSwitcher.isDark.value
    val colors = LocalAppColors.current

    val (color, icon, defaultTitle) = when (type) {
        AlertType.Note -> Triple(
            if (isDark) colors.Blue6 else colors.Blue4,
            Icons.Default.Info,
            "Note"
        )

        AlertType.Tip -> Triple(
            if (isDark) colors.Green6 else colors.Green4,
            Icons.Default.Lightbulb,
            "Tip"
        )

        AlertType.Important -> Triple(
            if (isDark) colors.Purple6 else colors.Purple4,
            Icons.Default.PriorityHigh,
            "Important"
        )

        AlertType.Warning -> Triple(
            if (isDark) colors.Yellow7 else colors.Yellow4,
            Icons.Default.Warning,
            "Warning"
        )

        AlertType.Caution -> Triple(
            if (isDark) colors.Red6 else colors.Red4,
            Icons.Default.Report,
            "Caution"
        )
    }

    val displayTitle = title ?: defaultTitle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Colored border
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(color)
        )

        // Content
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = displayTitle,
                    tint = color,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = displayTitle,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun AlertPreview() {
    DefaultPreview(true) {
        Alert(AlertType.Note, "This is a note")
        Alert(AlertType.Tip, "This is a note")
        Alert(AlertType.Important, "This is a note")
        Alert(AlertType.Caution, "This is a note")
        Alert(AlertType.Warning, "This is a note")
    }
}