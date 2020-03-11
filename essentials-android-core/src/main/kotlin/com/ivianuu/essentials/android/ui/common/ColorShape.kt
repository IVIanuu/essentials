package com.ivianuu.essentials.android.ui.common

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.layout.LayoutSize
import androidx.ui.unit.Size
import com.ivianuu.essentials.android.ui.material.Surface

@Composable
fun ColorShape(
    color: Color,
    shape: Shape = RectangleShape,
    size: Size? = null
) {
    Surface(
        modifier = size?.let { LayoutSize(it.width, it.height) } ?: Modifier.None,
        color = color,
        shape = shape
    ) {
    }
}