package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class DividerStyle(
    val color: Color,
    val modifier: Modifier
)

val DividerStyleAmbient = staticAmbientOf<DividerStyle>()

@Composable
fun DefaultDividerStyle(
    color: Color = contentColor().copy(alpha = 0.12f),
    modifier: Modifier = Modifier.None
) = DividerStyle(color = color, modifier = modifier)

@Composable
fun Divider(
    axis: Axis,
    modifier: Modifier = Modifier.None,
    style: DividerStyle = DividerStyleAmbient.currentOrElse { DefaultDividerStyle() }
) {
    val sizeModifiers = when (axis) {
        Axis.Horizontal -> LayoutWidth.Fill + LayoutHeight(1.dp) + style.modifier
        Axis.Vertical -> LayoutHeight.Fill + LayoutWidth(1.dp) + style.modifier
    }
    ColoredRect(
        color = style.color,
        modifier = modifier + sizeModifiers
    )
}