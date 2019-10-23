package com.ivianuu.essentials.ui.compose.image

import androidx.compose.Composable
import androidx.ui.core.dp
import androidx.ui.foundation.DrawImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Avatar(
    image: Image,
    tint: Color? = null
) = composable("Avatar") {
    Container(
        width = 40.dp,
        height = 40.dp
    ) {
        DrawImage(image = image, tint = tint)
    }
}