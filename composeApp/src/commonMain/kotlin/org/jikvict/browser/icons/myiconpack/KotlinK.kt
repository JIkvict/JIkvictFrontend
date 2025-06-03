package org.jikvict.browser.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.constant.LocalAppColors

val KotlinK: ImageVector
    @Composable
    get() {
        val colors = LocalAppColors.current
        val purpleColor = colors.Purple7

        return remember(purpleColor) {
            Builder(
                name = "KotlinK",
                defaultWidth = 128.dp,
                defaultHeight = 128.dp,
                viewportWidth = 91f,
                viewportHeight = 92f
            ).apply {
                path(
                    fill = SolidColor(purpleColor),
                    stroke = SolidColor(purpleColor),
                    strokeLineWidth = 1.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(90.1016f, 90.139f)
                    curveTo(90.4269f, 90.4508f, 90.2062f, 91f, 89.7556f, 91f)
                    horizontalLineTo(1.5f)
                    curveTo(1.2239f, 91f, 1f, 90.7761f, 1f, 90.5f)
                    verticalLineTo(1.5f)
                    curveTo(1f, 1.2239f, 1.2239f, 1f, 1.5f, 1f)
                    horizontalLineTo(89.7556f)
                    curveTo(90.2062f, 1f, 90.4269f, 1.5492f, 90.1016f, 1.861f)
                    lineTo(44.4203f, 45.639f)
                    curveTo(44.215f, 45.8358f, 44.215f, 46.1642f, 44.4203f, 46.361f)
                    lineTo(90.1016f, 90.139f)
                    close()
                }
            }.build()
        }
    }

@Preview
@Composable
private fun KotlinKPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = KotlinK, contentDescription = "KotlinK")
    }
}
