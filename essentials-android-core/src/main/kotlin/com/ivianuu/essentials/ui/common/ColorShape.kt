package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import com.ivianuu.essentials.ui.material.Surface

@Composable
fun ColorShape(
    modifier: Modifier = Modifier.None,
    color: Color,
    shape: Shape = RectangleShape
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = shape
    ) {
    }
}
