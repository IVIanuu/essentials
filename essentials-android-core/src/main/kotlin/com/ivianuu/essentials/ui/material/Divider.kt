package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.width
import androidx.ui.unit.dp

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = contentColor().copy(alpha = 0.12f)
) {
    Box(
        backgroundColor = color,
        modifier = Modifier.fillMaxWidth().height(1.dp) + modifier
    )
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = contentColor().copy(alpha = 0.12f)
) {
    Box(
        backgroundColor = color,
        modifier = Modifier.fillMaxHeight().width(1.dp) + modifier
    )
}
