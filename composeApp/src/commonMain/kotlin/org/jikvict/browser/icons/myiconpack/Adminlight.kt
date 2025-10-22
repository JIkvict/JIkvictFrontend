package org.jikvict.browser.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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
import org.jikvict.browser.util.LocalThemeSwitcherProvider

public val Adminlight: ImageVector
    get() {
        if (_adminlight != null) {
            return _adminlight!!
        }
        _adminlight = Builder(
            name = "Adminlight", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
            viewportWidth = 16.0f, viewportHeight = 16.0f
        ).apply {
            path(
                fill = SolidColor(LightTheme.colorScheme.primary), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(14.0f, 9.0f)
                curveTo(14.0f, 10.105f, 13.105f, 11.0f, 12.0f, 11.0f)
                curveTo(10.895f, 11.0f, 10.0f, 10.105f, 10.0f, 9.0f)
                curveTo(10.0f, 7.895f, 10.895f, 7.0f, 12.0f, 7.0f)
                curveTo(13.105f, 7.0f, 14.0f, 7.895f, 14.0f, 9.0f)
                close()
                moveTo(13.0f, 9.0f)
                curveTo(13.0f, 9.552f, 12.552f, 10.0f, 12.0f, 10.0f)
                curveTo(11.448f, 10.0f, 11.0f, 9.552f, 11.0f, 9.0f)
                curveTo(11.0f, 8.448f, 11.448f, 8.0f, 12.0f, 8.0f)
                curveTo(12.552f, 8.0f, 13.0f, 8.448f, 13.0f, 9.0f)
                close()
            }
            path(
                fill = SolidColor(LightTheme.colorScheme.primary), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(13.0f, 12.0f)
                horizontalLineTo(11.0f)
                curveTo(9.343f, 12.0f, 8.0f, 13.343f, 8.0f, 15.0f)
                curveTo(8.0f, 15.552f, 8.448f, 16.0f, 9.0f, 16.0f)
                horizontalLineTo(15.0f)
                curveTo(15.552f, 16.0f, 16.0f, 15.552f, 16.0f, 15.0f)
                curveTo(16.0f, 13.343f, 14.657f, 12.0f, 13.0f, 12.0f)
                close()
                moveTo(9.0f, 15.0f)
                curveTo(9.0f, 13.895f, 9.895f, 13.0f, 11.0f, 13.0f)
                horizontalLineTo(13.0f)
                curveTo(14.105f, 13.0f, 15.0f, 13.895f, 15.0f, 15.0f)
                horizontalLineTo(9.0f)
                close()
            }
            path(
                fill = SolidColor(Color.Transparent), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 10.0f)
                curveTo(12.552f, 10.0f, 13.0f, 9.552f, 13.0f, 9.0f)
                curveTo(13.0f, 8.448f, 12.552f, 8.0f, 12.0f, 8.0f)
                curveTo(11.448f, 8.0f, 11.0f, 8.448f, 11.0f, 9.0f)
                curveTo(11.0f, 9.552f, 11.448f, 10.0f, 12.0f, 10.0f)
                close()
            }
            path(
                fill = SolidColor(Color.Transparent), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.0f, 13.0f)
                curveTo(9.895f, 13.0f, 9.0f, 13.895f, 9.0f, 15.0f)
                horizontalLineTo(15.0f)
                curveTo(15.0f, 13.895f, 14.105f, 13.0f, 13.0f, 13.0f)
                horizontalLineTo(11.0f)
                close()
            }
            path(
                fill = SolidColor(Color.Transparent), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(14.0f, 3.5f)
                lineTo(8.0f, 1.0f)
                lineTo(2.0f, 3.5f)
                verticalLineTo(9.179f)
                curveTo(2.0f, 10.945f, 2.892f, 12.236f, 4.032f, 13.258f)
                curveTo(4.871f, 14.009f, 5.892f, 14.653f, 6.829f, 15.245f)
                curveTo(6.898f, 15.288f, 6.967f, 15.332f, 7.035f, 15.375f)
                curveTo(7.012f, 15.254f, 7.0f, 15.128f, 7.0f, 15.0f)
                curveTo(7.0f, 13.169f, 8.23f, 11.626f, 9.909f, 11.151f)
                curveTo(9.348f, 10.606f, 9.0f, 9.843f, 9.0f, 9.0f)
                curveTo(9.0f, 7.343f, 10.343f, 6.0f, 12.0f, 6.0f)
                curveTo(12.768f, 6.0f, 13.469f, 6.289f, 14.0f, 6.764f)
                lineTo(14.0f, 3.5f)
                close()
            }
            path(
                fill = SolidColor(LightTheme.colorScheme.onSurface), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.0f, 6.171f)
                verticalLineTo(4.167f)
                lineTo(8.0f, 2.083f)
                lineTo(3.0f, 4.167f)
                verticalLineTo(9.179f)
                curveTo(3.0f, 10.556f, 3.679f, 11.598f, 4.7f, 12.513f)
                curveTo(5.388f, 13.13f, 6.223f, 13.677f, 7.076f, 14.218f)
                curveTo(7.028f, 14.462f, 7.002f, 14.715f, 7.0f, 14.973f)
                curveTo(7.0f, 14.982f, 7.0f, 14.991f, 7.0f, 15.0f)
                curveTo(7.0f, 15.128f, 7.012f, 15.254f, 7.035f, 15.375f)
                curveTo(6.967f, 15.332f, 6.898f, 15.288f, 6.829f, 15.245f)
                curveTo(5.892f, 14.653f, 4.871f, 14.009f, 4.032f, 13.258f)
                curveTo(2.892f, 12.236f, 2.0f, 10.945f, 2.0f, 9.179f)
                verticalLineTo(3.5f)
                lineTo(8.0f, 1.0f)
                lineTo(14.0f, 3.5f)
                lineTo(14.0f, 6.764f)
                curveTo(13.712f, 6.506f, 13.373f, 6.302f, 13.0f, 6.171f)
                close()
            }
        }
            .build()
        return _adminlight!!
    }


@Composable
fun AdminIcon(): ImageVector {
    val theme = LocalThemeSwitcherProvider.current
    val isDark by theme.isDark
    return if (isDark) {
        Admindark
    } else {
        Adminlight
    }
}

private var _adminlight: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Admindark, contentDescription = "")
    }
}
