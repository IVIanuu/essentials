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
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    style: DividerStyle = DividerStyleAmbient.currentOrElse { DefaultDividerStyle() }
) {
    Box(
        backgroundColor = style.color,
        modifier = Modifier.fillMaxWidth().preferredHeight(1.dp) + style.modifier + modifier
    )
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    style: DividerStyle = DividerStyleAmbient.currentOrElse { DefaultDividerStyle() }
) {
    Box(
        backgroundColor = style.color,
        modifier = Modifier.fillMaxHeight().preferredWidth(1.dp) + style.modifier + modifier
    )
}
