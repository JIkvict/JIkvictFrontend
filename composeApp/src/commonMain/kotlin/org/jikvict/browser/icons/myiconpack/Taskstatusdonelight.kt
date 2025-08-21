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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Round
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.Unit
import org.jikvict.browser.icons.MyIconPack

public val MyIconPack.Taskstatusdonelight: ImageVector
    get() {
        if (_taskstatusdonelight != null) {
            return _taskstatusdonelight!!
        }
        _taskstatusdonelight = Builder(name = "Taskstatusdonelight", defaultWidth = 16.0.dp,
                defaultHeight = 16.0.dp, viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0xFFF2FCF3)), stroke = SolidColor(Color(0xFF208A3C)),
                    strokeLineWidth = 1.0f, strokeLineCap = Butt, strokeLineJoin = Round,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.0f, 5.5f)
                horizontalLineTo(12.0f)
                curveTo(12.276f, 5.5f, 12.5f, 5.724f, 12.5f, 6.0f)
                verticalLineTo(11.163f)
                curveTo(12.5f, 11.362f, 12.382f, 11.542f, 12.2f, 11.622f)
                lineTo(8.2f, 13.367f)
                curveTo(8.073f, 13.423f, 7.927f, 13.423f, 7.8f, 13.367f)
                lineTo(3.8f, 11.622f)
                curveTo(3.618f, 11.542f, 3.5f, 11.362f, 3.5f, 11.163f)
                verticalLineTo(6.0f)
                curveTo(3.5f, 5.724f, 3.724f, 5.5f, 4.0f, 5.5f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF2FCF3)), stroke = SolidColor(Color(0xFF208A3C)),
                    strokeLineWidth = 1.0f, strokeLineCap = Butt, strokeLineJoin = Round,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.0f, 1.5f)
                lineTo(15.5f, 5.5f)
                lineTo(8.0f, 9.5f)
                lineTo(0.5f, 5.5f)
                lineTo(8.0f, 1.5f)
                close()
            }
        }
        .build()
        return _taskstatusdonelight!!
    }

private var _taskstatusdonelight: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MyIconPack.Taskstatusdonelight, contentDescription = "")
    }
}
