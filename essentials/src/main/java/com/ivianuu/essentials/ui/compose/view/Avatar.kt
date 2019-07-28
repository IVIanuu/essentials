package com.ivianuu.essentials.ui.compose.view

import androidx.compose.ViewComposition
import androidx.ui.core.dp

fun ViewComposition.Avatar(image: Image) = ImageView {
    size(40.dp)
    image(image)
}