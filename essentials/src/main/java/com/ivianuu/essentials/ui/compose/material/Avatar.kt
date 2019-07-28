package com.ivianuu.essentials.ui.compose.material

import androidx.compose.ViewComposition
import androidx.ui.core.dp
import com.ivianuu.essentials.ui.compose.view.Image
import com.ivianuu.essentials.ui.compose.view.ImageView
import com.ivianuu.essentials.ui.compose.view.image
import com.ivianuu.essentials.ui.compose.view.size

fun ViewComposition.Avatar(image: Image) = ImageView {
    size(40.dp)
    image(image)
}