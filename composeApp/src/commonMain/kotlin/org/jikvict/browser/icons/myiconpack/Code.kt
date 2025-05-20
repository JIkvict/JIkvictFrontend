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
import org.jikvict.browser.icons.MyIconPack

public val MyIconPack.Code: ImageVector
    get() {
        if (_code != null) {
            return _code!!
        }
        _code = Builder(
            name = "Code", defaultWidth = 24.0.dp, defaultHeight = 16.0.dp,
            viewportWidth = 24.0f, viewportHeight = 16.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFCED0D6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(8.32f, 1.923f)
                curveTo(8.655f, 1.597f, 8.655f, 1.07f, 8.32f, 0.744f)
                curveTo(7.986f, 0.419f, 7.443f, 0.419f, 7.108f, 0.744f)
                lineTo(0.251f, 7.411f)
                curveTo(-0.084f, 7.736f, -0.084f, 8.264f, 0.251f, 8.589f)
                lineTo(7.108f, 15.256f)
                curveTo(7.443f, 15.581f, 7.986f, 15.581f, 8.32f, 15.256f)
                curveTo(8.655f, 14.931f, 8.655f, 14.403f, 8.32f, 14.077f)
                lineTo(2.069f, 8.0f)
                lineTo(8.32f, 1.923f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCED0D6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(16.892f, 0.744f)
                curveTo(16.557f, 0.419f, 16.014f, 0.419f, 15.68f, 0.744f)
                curveTo(15.345f, 1.07f, 15.345f, 1.597f, 15.68f, 1.923f)
                lineTo(21.931f, 8.0f)
                lineTo(15.68f, 14.077f)
                curveTo(15.345f, 14.403f, 15.345f, 14.931f, 15.68f, 15.256f)
                curveTo(16.014f, 15.581f, 16.557f, 15.581f, 16.892f, 15.256f)
                lineTo(23.749f, 8.589f)
                curveTo(24.084f, 8.264f, 24.084f, 7.736f, 23.749f, 7.411f)
                lineTo(16.892f, 0.744f)
                close()
            }
        }
            .build()
        return _code!!
    }

private var _code: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.Code, contentDescription = "")
    }
}
