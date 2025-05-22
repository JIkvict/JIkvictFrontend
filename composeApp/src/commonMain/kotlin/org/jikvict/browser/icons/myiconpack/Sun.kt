package org.jikvict.browser.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import org.jikvict.browser.theme.DarkTheme

val Sun: ImageVector
    get() {
        val color = DarkTheme.colorScheme.onSurface
        if (_sun != null) {
            return _sun!!
        }
        _sun = Builder(
            name = "Sun", defaultWidth = 800.0.dp, defaultHeight = 800.0.dp,
            viewportWidth = 35.0f, viewportHeight = 35.0f
        ).apply {
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.5f, 25.88f)
                arcToRelative(8.38f, 8.38f, 0.0f, true, true, 8.38f, -8.38f)
                arcTo(8.389f, 8.389f, 0.0f, false, true, 17.5f, 25.88f)
                close()
                moveTo(17.5f, 11.62f)
                arcToRelative(5.88f, 5.88f, 0.0f, true, false, 5.88f, 5.88f)
                arcTo(5.887f, 5.887f, 0.0f, false, false, 17.5f, 11.62f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.5f, 5.471f)
                horizontalLineToRelative(-0.034f)
                arcTo(1.251f, 1.251f, 0.0f, false, true, 16.25f, 4.187f)
                lineToRelative(0.075f, -2.721f)
                arcTo(1.267f, 1.267f, 0.0f, false, true, 17.609f, 0.25f)
                arcToRelative(1.251f, 1.251f, 0.0f, false, true, 1.215f, 1.284f)
                lineToRelative(-0.075f, 2.721f)
                arcTo(1.249f, 1.249f, 0.0f, false, true, 17.5f, 5.471f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.893f, 9.364f)
                arcToRelative(1.25f, 1.25f, 0.0f, false, true, -0.859f, -2.158f)
                lineToRelative(1.978f, -1.871f)
                arcTo(1.25f, 1.25f, 0.0f, false, true, 29.73f, 7.151f)
                lineTo(27.752f, 9.022f)
                arcTo(1.242f, 1.242f, 0.0f, false, true, 26.893f, 9.364f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(33.5f, 18.837f)
                horizontalLineToRelative(-0.036f)
                lineToRelative(-2.722f, -0.077f)
                arcToRelative(1.249f, 1.249f, 0.0f, false, true, -1.213f, -1.284f)
                arcToRelative(1.211f, 1.211f, 0.0f, false, true, 1.285f, -1.214f)
                lineToRelative(2.721f, 0.077f)
                arcToRelative(1.25f, 1.25f, 0.0f, false, true, -0.035f, 2.5f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.748f, 30.13f)
                arcToRelative(1.248f, 1.248f, 0.0f, false, true, -0.909f, -0.392f)
                lineTo(25.97f, 27.759f)
                arcToRelative(1.25f, 1.25f, 0.0f, true, true, 1.817f, -1.717f)
                lineToRelative(1.869f, 1.98f)
                arcToRelative(1.249f, 1.249f, 0.0f, false, true, -0.908f, 2.108f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.4f, 34.75f)
                horizontalLineToRelative(-0.037f)
                arcToRelative(1.249f, 1.249f, 0.0f, false, true, -1.213f, -1.285f)
                lineToRelative(0.079f, -2.721f)
                arcToRelative(1.25f, 1.25f, 0.0f, false, true, 2.5f, 0.072f)
                lineToRelative(-0.079f, 2.721f)
                arcTo(1.249f, 1.249f, 0.0f, false, true, 17.4f, 34.75f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(6.112f, 29.989f)
                arcToRelative(1.249f, 1.249f, 0.0f, false, true, -0.857f, -2.159f)
                lineToRelative(1.98f, -1.867f)
                arcTo(1.25f, 1.25f, 0.0f, true, true, 8.95f, 27.781f)
                lineTo(6.969f, 29.648f)
                arcTo(1.242f, 1.242f, 0.0f, false, true, 6.112f, 29.989f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(4.221f, 18.72f)
                horizontalLineTo(4.184f)
                lineToRelative(-2.721f, -0.081f)
                arcTo(1.25f, 1.25f, 0.0f, false, true, 0.251f, 17.352f)
                arcTo(1.237f, 1.237f, 0.0f, false, true, 1.537f, 16.14f)
                lineToRelative(2.721f, 0.081f)
                arcToRelative(1.25f, 1.25f, 0.0f, false, true, -0.037f, 2.5f)
                close()
            }
            path(
                fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(8.135f, 9.335f)
                arcToRelative(1.248f, 1.248f, 0.0f, false, true, -0.91f, -0.393f)
                lineTo(5.359f, 6.961f)
                arcToRelative(1.25f, 1.25f, 0.0f, true, true, 1.82f, -1.715f)
                lineTo(9.046f, 7.228f)
                arcToRelative(1.251f, 1.251f, 0.0f, false, true, -0.911f, 2.107f)
                close()
            }
        }
            .build()
        return _sun!!
    }

private var _sun: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Sun, contentDescription = "")
    }
}
