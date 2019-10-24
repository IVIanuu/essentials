package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun ImageButton(
    image: Image,
    tint: Color? = null,
    width: Dp = DefaultImageButtonSize,
    height: Dp = DefaultImageButtonSize,
    onClick: (() -> Unit)? = null
) = composable("ImageButton") {
    Ripple(bounded = false, enabled = onClick != null) {
        Clickable(onClick = onClick) {
            Container(width = width, height = height) {
                SimpleImage(image, tint)
            }
        }
    }
}

private val DefaultImageButtonSize = 40.dp