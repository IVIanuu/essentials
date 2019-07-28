package com.ivianuu.essentials.ui.compose.view

import androidx.compose.ViewComposition
import androidx.ui.core.currentTextStyle
import androidx.ui.core.dp

fun ViewComposition.Icon(image: Image) = ImageView {
    size(24.dp)
    image(image)
    (+currentTextStyle()).color?.let { imageColor(it) }
}