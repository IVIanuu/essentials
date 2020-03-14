package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class DividerStyle(
    val color: Color,
    val size: Dp
)

val DividerStyleAmbient = staticAmbientOf<DividerStyle>()

@Composable
fun DefaultDividerStyle(
    color: Color = contentColor().copy(alpha = 0.12f),
    size: Dp = 1.dp
) = DividerStyle(color = color, size = size)

@Composable
fun Divider(
    axis: Axis,
    modifier: Modifier = Modifier.None,
    style: DividerStyle = DividerStyleAmbient.currentOrElse { DefaultDividerStyle() }
) {
    val sizeModifiers = remember(axis, style.size) {
        when (axis) {
            Axis.Horizontal -> LayoutWidth.Fill + LayoutHeight(style.size)
            Axis.Vertical -> LayoutHeight.Fill + LayoutWidth(style.size)
        }
    }
    ColoredRect(
        color = style.color,
        modifier = modifier + sizeModifiers
    )
}