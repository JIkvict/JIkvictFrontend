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
import org.jikvict.browser.theme.LightTheme

val Moon: ImageVector
    get() {
        val color = LightTheme.colorScheme.onSurface
        if (_moon != null) {
            return _moon!!
        }
        _moon =
            Builder(
                name = "Moon",
                defaultWidth = 800.0.dp,
                defaultHeight = 800.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(color),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(21.067f, 11.857f)
                    lineTo(20.425f, 11.469f)
                    lineTo(21.067f, 11.857f)
                    close()
                    moveTo(12.143f, 2.933f)
                    lineTo(11.755f, 2.291f)
                    verticalLineTo(2.291f)
                    lineTo(12.143f, 2.933f)
                    close()
                    moveTo(21.25f, 12.0f)
                    curveTo(21.25f, 17.109f, 17.109f, 21.25f, 12.0f, 21.25f)
                    verticalLineTo(22.75f)
                    curveTo(17.937f, 22.75f, 22.75f, 17.937f, 22.75f, 12.0f)
                    horizontalLineTo(21.25f)
                    close()
                    moveTo(12.0f, 21.25f)
                    curveTo(6.891f, 21.25f, 2.75f, 17.109f, 2.75f, 12.0f)
                    horizontalLineTo(1.25f)
                    curveTo(1.25f, 17.937f, 6.063f, 22.75f, 12.0f, 22.75f)
                    verticalLineTo(21.25f)
                    close()
                    moveTo(2.75f, 12.0f)
                    curveTo(2.75f, 6.891f, 6.891f, 2.75f, 12.0f, 2.75f)
                    verticalLineTo(1.25f)
                    curveTo(6.063f, 1.25f, 1.25f, 6.063f, 1.25f, 12.0f)
                    horizontalLineTo(2.75f)
                    close()
                    moveTo(15.5f, 14.25f)
                    curveTo(12.324f, 14.25f, 9.75f, 11.676f, 9.75f, 8.5f)
                    horizontalLineTo(8.25f)
                    curveTo(8.25f, 12.504f, 11.496f, 15.75f, 15.5f, 15.75f)
                    verticalLineTo(14.25f)
                    close()
                    moveTo(20.425f, 11.469f)
                    curveTo(19.417f, 13.137f, 17.588f, 14.25f, 15.5f, 14.25f)
                    verticalLineTo(15.75f)
                    curveTo(18.135f, 15.75f, 20.441f, 14.344f, 21.709f, 12.245f)
                    lineTo(20.425f, 11.469f)
                    close()
                    moveTo(9.75f, 8.5f)
                    curveTo(9.75f, 6.412f, 10.863f, 4.583f, 12.531f, 3.575f)
                    lineTo(11.755f, 2.291f)
                    curveTo(9.656f, 3.559f, 8.25f, 5.865f, 8.25f, 8.5f)
                    horizontalLineTo(9.75f)
                    close()
                    moveTo(12.0f, 2.75f)
                    curveTo(11.911f, 2.75f, 11.808f, 2.71f, 11.732f, 2.632f)
                    curveTo(11.669f, 2.565f, 11.654f, 2.502f, 11.65f, 2.477f)
                    curveTo(11.646f, 2.446f, 11.648f, 2.356f, 11.755f, 2.291f)
                    lineTo(12.531f, 3.575f)
                    curveTo(13.034f, 3.271f, 13.196f, 2.714f, 13.137f, 2.276f)
                    curveTo(13.075f, 1.821f, 12.717f, 1.25f, 12.0f, 1.25f)
                    verticalLineTo(2.75f)
                    close()
                    moveTo(21.709f, 12.245f)
                    curveTo(21.644f, 12.352f, 21.554f, 12.354f, 21.523f, 12.35f)
                    curveTo(21.498f, 12.346f, 21.435f, 12.331f, 21.368f, 12.268f)
                    curveTo(21.29f, 12.192f, 21.25f, 12.089f, 21.25f, 12.0f)
                    horizontalLineTo(22.75f)
                    curveTo(22.75f, 11.283f, 22.179f, 10.925f, 21.724f, 10.863f)
                    curveTo(21.286f, 10.804f, 20.729f, 10.966f, 20.425f, 11.469f)
                    lineTo(21.709f, 12.245f)
                    close()
                }
            }.build()
        return _moon!!
    }

private var _moon: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Moon, contentDescription = "")
    }
}
