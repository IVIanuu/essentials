package com.ivianuu.essentials.ui.compose.material

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
        width = AvatarSize,
        height = AvatarSize
    ) {
        DrawImage(image = image, tint = tint)
    }
}

private val AvatarSize = 40.dp