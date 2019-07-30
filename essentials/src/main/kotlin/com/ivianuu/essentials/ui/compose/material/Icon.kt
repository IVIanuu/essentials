package com.ivianuu.essentials.ui.compose.material

import androidx.compose.ViewComposition
import androidx.ui.core.currentTextStyle
import androidx.ui.core.dp
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.ImageView
import com.ivianuu.essentials.ui.compose.view.image
import com.ivianuu.essentials.ui.compose.view.imageColor
import com.ivianuu.essentials.ui.compose.view.size

inline fun ViewComposition.Icon(image: Image) = Icon(sourceLocation(), image)

fun ViewComposition.Icon(key: Any, image: Image) = ImageView(key) {
    size(24.dp)
    image(image)
    (+currentTextStyle()).color?.let { imageColor(it) }
}