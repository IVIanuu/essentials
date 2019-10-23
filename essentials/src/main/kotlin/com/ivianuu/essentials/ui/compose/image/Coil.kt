package com.ivianuu.essentials.ui.compose.image

import androidx.compose.Composable
import androidx.compose.Observe
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.ui.graphics.Image
import coil.ImageLoader
import coil.api.getAny
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.load
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.resources.drawableResource

fun loadCoilImageAny(
    placeholder: Image,
    data: Any
) = effectOf<Image> {
    val imageLoader = +inject<ImageLoader>()
    return@effectOf +load(placeholder = placeholder) {
        return@load imageLoader.getAny(data).toImage()
    }
}

@Composable
fun CoilImageAny(
    data: Any,
    placeholder: Image? = null,
    image: @Composable() (Image) -> Unit
) = composable(data) {
    val wasPlaceholderNull = placeholder == null
    // todo better default placeholder
    val placeholder =
        placeholder ?: +drawableResource(R.drawable.transparent_rect) // todo
    Observe {
        val loadedImage = +loadCoilImageAny(
            placeholder = placeholder,
            data = data
        )

        if (!wasPlaceholderNull || loadedImage != placeholder) {
            image(loadedImage)
        }
    }
}