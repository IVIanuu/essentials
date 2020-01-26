/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.coil

import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.ambientOf
import androidx.ui.core.Modifier
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.ambientDensity
import androidx.ui.foundation.DrawImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.unit.Size
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import androidx.ui.unit.withDensity
import coil.ImageLoader
import coil.request.GetRequestBuilder
import com.ivianuu.essentials.ui.coroutines.load
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.painter.DrawRenderable
import com.ivianuu.essentials.ui.painter.ImageRenderable
import com.ivianuu.essentials.ui.painter.Renderable

@Stable
class CoilRenderable(
    private val data: Any,
    private val tintColor: Color? = null,
    private val size: Size? = null
) : Renderable {
    @Composable
    override fun content() {
        val image = loadImage(data = data, size = size)
        val density = ambientDensity()
        Container(
            width = size?.width ?: withDensity(density) { image?.width?.ipx?.toDp() } ?: 0.dp,
            height = size?.height ?: withDensity(density) { image?.height?.ipx?.toDp() } ?: 0.dp
        ) {
            if (image != null) {
                RepaintBoundary {
                    DrawImage(image = image, tint = tintColor)
                }
            }
        }
    }
}

@Composable
fun loadImage(
    data: Any,
    placeholder: Image? = PlaceholderAmbient.current,
    size: Size? = null,
    imageLoader: ImageLoader = inject()
): Image? {
    return if (placeholder != null) {
        loadImage(data = data, placeholder = placeholder, size = size, imageLoader = imageLoader)
    } else {
        loadImage(data = data, imageLoader = imageLoader, size = size)
    }
}

@Composable
fun loadImage(
    data: Any,
    placeholder: Image,
    size: Size? = null,
    imageLoader: ImageLoader = inject()
): Image = loadImage(data = data, size = size, imageLoader = imageLoader) ?: placeholder

// todo ir
suspend fun ImageLoader.getAnyNoInline(
    data: Any,
    builder: GetRequestBuilder.() -> Unit = {}
): Drawable = get(GetRequestBuilder(defaults).data(data).apply(builder).build())

@Composable
fun loadImage(
    data: Any,
    size: Size? = null,
    imageLoader: ImageLoader = inject()
): Image? {
    val density = ambientDensity()
    return load(placeholder = null, key = listOf(data, size, imageLoader)) {
        imageLoader.getAnyNoInline(data) {
            if (size != null) {
                withDensity(density) {
                    size(
                        width = size.width.toIntPx().value,
                        height = size.height.toIntPx().value
                    )
                }
            }
        }.toImage()
    }
}

val PlaceholderAmbient =
    ambientOf<Image?> { null }

@Composable
fun Image(
    data: Any,
    modifier: Modifier = Modifier.None,
    placeholder: Image? = PlaceholderAmbient.current,
    imageLoader: ImageLoader = inject(),
    image: @Composable (Image?) -> Unit = {
        if (it != null) DrawRenderable(ImageRenderable(it))
    }
) {
    val loadedImage = loadImage(
        placeholder = placeholder,
        data = data,
        imageLoader = imageLoader
    )

    RepaintBoundary {
        Container(modifier = modifier) {
            image(loadedImage)
        }
    }
}

@JvmName("ImageNonNullPlaceholder")
@Composable
fun Image(
    data: Any,
    modifier: Modifier = Modifier.None,
    placeholder: Image,
    imageLoader: ImageLoader = inject(),
    image: @Composable (Image) -> Unit = {
        DrawRenderable(ImageRenderable(it))
    }) {
    val loadedImage = loadImage(
        placeholder = placeholder,
        data = data,
        imageLoader = imageLoader
    )

    RepaintBoundary {
        Container(modifier = modifier) {
            image(loadedImage)
        }
    }
}
