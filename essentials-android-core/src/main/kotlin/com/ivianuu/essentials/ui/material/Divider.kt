package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
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
    modifier: Modifier = Modifier
) = DividerStyle(color = color, modifier = modifier)

@Composable
fun Divider(
    axis: Axis,
    modifier: Modifier = Modifier,
    style: DividerStyle = DividerStyleAmbient.currentOrElse { DefaultDividerStyle() }
) {
    val sizeModifiers = when (axis) {
        Axis.Horizontal -> Modifier.fillMaxWidth().preferredHeight(1.dp).plus(style.modifier)
        Axis.Vertical -> Modifier.fillMaxHeight().preferredWidth(1.dp).plus(style.modifier)
    }
    Box(
        backgroundColor = style.color,
        modifier = modifier.plus(sizeModifiers)
    )
}