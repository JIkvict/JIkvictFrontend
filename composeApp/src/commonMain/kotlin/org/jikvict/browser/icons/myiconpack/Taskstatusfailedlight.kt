package org.jikvict.browser.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.Unit
import org.jikvict.browser.icons.MyIconPack

public val MyIconPack.Taskstatusfailedlight: ImageVector
    get() {
        if (_taskstatusfailedlight != null) {
            return _taskstatusfailedlight!!
        }
        _taskstatusfailedlight = Builder(name = "Taskstatusfailedlight", defaultWidth = 16.0.dp,
                defaultHeight = 16.0.dp, viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0xFFFFF7F7)), stroke = SolidColor(Color(0xFFDB3B4B)),
                    strokeLineWidth = 1.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.0f, 2.5f)
                horizontalLineTo(12.0f)
                curveTo(12.828f, 2.5f, 13.5f, 3.172f, 13.5f, 4.0f)
                verticalLineTo(12.0f)
                curveTo(13.5f, 12.828f, 12.828f, 13.5f, 12.0f, 13.5f)
                horizontalLineTo(4.0f)
                curveTo(3.172f, 13.5f, 2.5f, 12.828f, 2.5f, 12.0f)
                verticalLineTo(4.0f)
                curveTo(2.5f, 3.172f, 3.172f, 2.5f, 4.0f, 2.5f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFDB3B4B)),
                    strokeLineWidth = 1.0f, strokeLineCap = Round, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.5f, 10.5f)
                lineTo(10.5f, 5.5f)
                moveTo(5.5f, 5.5f)
                lineTo(10.5f, 10.5f)
            }
        }
        .build()
        return _taskstatusfailedlight!!
    }

private var _taskstatusfailedlight: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.Taskstatusfailedlight, contentDescription = "")
    }
}
