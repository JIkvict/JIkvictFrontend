package org.jikvict.browser.icons.myiconpack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import org.jikvict.browser.util.LocalThemeSwitcherProvider

public val Githublight: ImageVector
    get() {
        if (_githublight != null) {
            return _githublight!!
        }
        _githublight = Builder(
            name = "Githublight", defaultWidth = 16.0.dp, defaultHeight =
                16.0.dp, viewportWidth = 16.0f, viewportHeight = 16.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF161514)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(7.997f, 1.0f)
                curveTo(4.133f, 1.0f, 1.0f, 4.133f, 1.0f, 7.998f)
                curveTo(1.0f, 11.09f, 3.005f, 13.712f, 5.786f, 14.638f)
                curveTo(6.136f, 14.702f, 6.263f, 14.486f, 6.263f, 14.3f)
                curveTo(6.263f, 14.134f, 6.257f, 13.694f, 6.254f, 13.11f)
                curveTo(4.307f, 13.533f, 3.897f, 12.172f, 3.897f, 12.172f)
                curveTo(3.578f, 11.364f, 3.12f, 11.149f, 3.12f, 11.149f)
                curveTo(2.484f, 10.715f, 3.168f, 10.723f, 3.168f, 10.723f)
                curveTo(3.87f, 10.773f, 4.24f, 11.444f, 4.24f, 11.444f)
                curveTo(4.864f, 12.514f, 5.878f, 12.205f, 6.276f, 12.026f)
                curveTo(6.34f, 11.574f, 6.521f, 11.265f, 6.72f, 11.09f)
                curveTo(5.167f, 10.914f, 3.533f, 10.313f, 3.533f, 7.632f)
                curveTo(3.533f, 6.868f, 3.806f, 6.243f, 4.253f, 5.754f)
                curveTo(4.181f, 5.577f, 3.941f, 4.865f, 4.322f, 3.902f)
                curveTo(4.322f, 3.902f, 4.909f, 3.714f, 6.246f, 4.62f)
                curveTo(6.804f, 4.464f, 7.403f, 4.387f, 7.998f, 4.384f)
                curveTo(8.592f, 4.387f, 9.191f, 4.464f, 9.75f, 4.62f)
                curveTo(11.086f, 3.714f, 11.672f, 3.902f, 11.672f, 3.902f)
                curveTo(12.054f, 4.865f, 11.814f, 5.577f, 11.742f, 5.754f)
                curveTo(12.191f, 6.243f, 12.461f, 6.868f, 12.461f, 7.632f)
                curveTo(12.461f, 10.32f, 10.825f, 10.912f, 9.267f, 11.085f)
                curveTo(9.517f, 11.301f, 9.741f, 11.728f, 9.741f, 12.381f)
                curveTo(9.741f, 13.316f, 9.733f, 14.071f, 9.733f, 14.3f)
                curveTo(9.733f, 14.488f, 9.859f, 14.705f, 10.214f, 14.637f)
                curveTo(12.992f, 13.71f, 14.995f, 11.089f, 14.995f, 7.998f)
                curveTo(14.995f, 4.133f, 11.862f, 1.0f, 7.997f, 1.0f)
                close()
            }
        }
            .build()
        return _githublight!!
    }

private var _githublight: ImageVector? = null


@Composable
fun GitHubIcon(): ImageVector {
    val theme = LocalThemeSwitcherProvider.current
    val isDark by theme.isDark
    return if (isDark) {
        Githubdark
    } else {
        Githublight
    }
}