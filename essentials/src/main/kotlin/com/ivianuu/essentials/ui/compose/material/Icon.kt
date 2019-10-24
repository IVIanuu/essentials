package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.foundation.DrawImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Icon(
    image: Image,
    tint: Color?
) = composable("Icon") {
    Container(
        width = IconSize,
        height = IconSize
    ) {
        DrawImage(image = image, tint = tint)
    }
}

private val IconSize = 24.dp