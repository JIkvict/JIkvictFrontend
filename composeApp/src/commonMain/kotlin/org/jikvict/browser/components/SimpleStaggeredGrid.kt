package org.jikvict.browser.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

@Composable
fun SimpleStaggeredGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    verticalSpacing: Int = 0,
    horizontalSpacing: Int = 0,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->

        val safeColumns = columns.coerceAtLeast(1)
        val totalSpacing = horizontalSpacing * (safeColumns - 1)
        val effectiveWidth = (constraints.maxWidth - totalSpacing).coerceAtLeast(0)
        val columnWidth = effectiveWidth / safeColumns

        val columnWidths = IntArray(safeColumns)
        val columnHeights = IntArray(safeColumns)

        val placeables = measurables.map { measurable ->
            val column = columnHeights.indices.minByOrNull { columnHeights[it] } ?: 0

            val placeable = measurable.measure(
                Constraints(
                    minWidth = 0,
                    maxWidth = columnWidth.coerceAtLeast(0),
                    minHeight = 0,
                    maxHeight = Constraints.Infinity
                )
            )

            columnWidths[column] = maxOf(columnWidths[column], placeable.width)
            if (columnHeights[column] > 0) columnHeights[column] += verticalSpacing
            columnHeights[column] += placeable.height

            column to placeable
        }

        val layoutWidth = columnWidths.sum() + horizontalSpacing * (safeColumns - 1)
        val layoutHeight = columnHeights.maxOrNull() ?: 0

        val columnX = IntArray(safeColumns)
        for (i in 1 until safeColumns) {
            columnX[i] = columnX[i - 1] + columnWidths[i - 1] + horizontalSpacing
        }

        val columnY = IntArray(safeColumns)

        layout(layoutWidth.coerceIn(constraints.minWidth, constraints.maxWidth), layoutHeight) {
            placeables.forEach { (column, placeable) ->
                placeable.placeRelative(
                    x = columnX[column],
                    y = columnY[column]
                )
                columnY[column] += placeable.height + verticalSpacing
            }
        }
    }
}
