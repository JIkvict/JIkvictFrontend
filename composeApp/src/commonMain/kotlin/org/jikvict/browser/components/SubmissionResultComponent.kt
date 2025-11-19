package org.jikvict.browser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jikvict.api.models.AssignmentResultDto
import org.jikvict.browser.screens.OutlinedContentContainer
import org.jikvict.browser.screens.TestResultCard
import org.jikvict.browser.util.LocalThemeSwitcherProvider

@Composable
fun SubmissionResultComponent(
    assignmentResultDto: AssignmentResultDto
) {
    val theme = LocalThemeSwitcherProvider.current
    val isDark by theme.isDark

    OutlinedContentContainer(
        label = "${assignmentResultDto.timeStamp} (${assignmentResultDto.points} points)"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            assignmentResultDto.result?.testResults?.let { testResults ->
                testResults.forEach { testResult ->
                    var isExpanded by remember { mutableStateOf(false) }
                    TestResultCard(
                        testResult = testResult,
                        isDark = isDark,
                        isExpanded = isExpanded,
                        onClick = { isExpanded = !isExpanded }
                    )
                }
            } ?: run {
                Text(
                    text = "No detailed test results available for this attempt.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}