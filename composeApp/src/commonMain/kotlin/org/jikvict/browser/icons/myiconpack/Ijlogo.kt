package org.jikvict.browser.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

val Ijlogo: ImageVector
    get() {
        if (_ijlogo != null) {
            return _ijlogo!!
        }
        _ijlogo =
            Builder(
                name = "Ijlogo",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFFA177F4)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(2.4f, 0.0f)
                    curveTo(1.074f, 0.0f, 0.0f, 1.007f, 0.0f, 2.25f)
                    curveTo(0.0f, 3.493f, 1.074f, 4.5f, 2.4f, 4.5f)
                    horizontalLineTo(9.598f)
                    verticalLineTo(12.75f)
                    curveTo(9.598f, 16.478f, 6.375f, 19.5f, 2.4f, 19.5f)
                    curveTo(1.074f, 19.5f, 0.0f, 20.507f, 0.0f, 21.75f)
                    curveTo(0.0f, 22.993f, 1.074f, 24.0f, 2.4f, 24.0f)
                    curveTo(9.026f, 24.0f, 14.398f, 18.963f, 14.398f, 12.75f)
                    verticalLineTo(2.625f)
                    curveTo(14.398f, 1.175f, 13.144f, 0.0f, 11.598f, 0.0f)
                    horizontalLineTo(2.4f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFA177F4)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(24.0f, 2.25f)
                    curveTo(24.0f, 1.007f, 22.926f, -0.0f, 21.6f, -0.0f)
                    curveTo(20.275f, -0.0f, 19.201f, 1.007f, 19.201f, 2.25f)
                    verticalLineTo(21.75f)
                    curveTo(19.201f, 22.993f, 20.275f, 24.0f, 21.6f, 24.0f)
                    curveTo(22.926f, 24.0f, 24.0f, 22.993f, 24.0f, 21.75f)
                    verticalLineTo(2.25f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFDB5C5C)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(3.5f, 11.5f)
                    moveToRelative(-3.5f, 0.0f)
                    arcToRelative(3.5f, 3.5f, 0.0f, true, true, 7.0f, 0.0f)
                    arcToRelative(3.5f, 3.5f, 0.0f, true, true, -7.0f, 0.0f)
                }
            }.build()
        return _ijlogo!!
    }

private var _ijlogo: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Ijlogo, contentDescription = "")
    }
}
