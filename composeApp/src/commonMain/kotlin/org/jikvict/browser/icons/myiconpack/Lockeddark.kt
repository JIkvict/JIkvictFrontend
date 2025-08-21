package org.jikvict.browser.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.Unit
import org.jikvict.browser.icons.MyIconPack

public val MyIconPack.Lockeddark: ImageVector
    get() {
        if (_lockeddark != null) {
            return _lockeddark!!
        }
        _lockeddark = Builder(name = "Lockeddark", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0xFFCED0D6)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(5.0f, 5.0f)
                curveTo(5.0f, 3.343f, 6.343f, 2.0f, 8.0f, 2.0f)
                curveTo(9.657f, 2.0f, 11.0f, 3.343f, 11.0f, 5.0f)
                verticalLineTo(6.0f)
                curveTo(12.105f, 6.0f, 13.0f, 6.895f, 13.0f, 8.0f)
                verticalLineTo(12.0f)
                curveTo(13.0f, 13.105f, 12.105f, 14.0f, 11.0f, 14.0f)
                horizontalLineTo(5.0f)
                curveTo(3.895f, 14.0f, 3.0f, 13.105f, 3.0f, 12.0f)
                verticalLineTo(8.0f)
                curveTo(3.0f, 6.895f, 3.895f, 6.0f, 5.0f, 6.0f)
                verticalLineTo(5.0f)
                close()
                moveTo(10.0f, 5.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(5.0f)
                curveTo(6.0f, 3.895f, 6.895f, 3.0f, 8.0f, 3.0f)
                curveTo(9.105f, 3.0f, 10.0f, 3.895f, 10.0f, 5.0f)
                close()
                moveTo(5.0f, 7.0f)
                curveTo(4.448f, 7.0f, 4.0f, 7.448f, 4.0f, 8.0f)
                verticalLineTo(12.0f)
                curveTo(4.0f, 12.552f, 4.448f, 13.0f, 5.0f, 13.0f)
                horizontalLineTo(11.0f)
                curveTo(11.552f, 13.0f, 12.0f, 12.552f, 12.0f, 12.0f)
                verticalLineTo(8.0f)
                curveTo(12.0f, 7.448f, 11.552f, 7.0f, 11.0f, 7.0f)
                horizontalLineTo(5.0f)
                close()
                moveTo(8.0f, 8.5f)
                curveTo(7.724f, 8.5f, 7.5f, 8.724f, 7.5f, 9.0f)
                verticalLineTo(11.0f)
                curveTo(7.5f, 11.276f, 7.724f, 11.5f, 8.0f, 11.5f)
                curveTo(8.276f, 11.5f, 8.5f, 11.276f, 8.5f, 11.0f)
                verticalLineTo(9.0f)
                curveTo(8.5f, 8.724f, 8.276f, 8.5f, 8.0f, 8.5f)
                close()
            }
        }
        .build()
        return _lockeddark!!
    }

private var _lockeddark: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.Lockeddark, contentDescription = "")
    }
}
