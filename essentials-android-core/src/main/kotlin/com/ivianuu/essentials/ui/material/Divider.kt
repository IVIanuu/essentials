package com.ivianuu.essentials.ui.material

import androidx.compose.foundation.Box
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
