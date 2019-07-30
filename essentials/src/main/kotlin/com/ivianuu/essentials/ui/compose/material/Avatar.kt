package com.ivianuu.essentials.ui.compose.material

import androidx.compose.ViewComposition
import androidx.ui.core.dp
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.ImageView
import com.ivianuu.essentials.ui.compose.view.image
import com.ivianuu.essentials.ui.compose.view.size

inline fun ViewComposition.Avatar(image: Image) = Avatar(sourceLocation(), image)

fun ViewComposition.Avatar(key: Any, image: Image) = ImageView(key) {
    size(40.dp)
    image(image)
}