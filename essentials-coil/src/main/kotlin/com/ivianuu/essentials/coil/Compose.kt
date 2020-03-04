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
import androidx.compose.staticAmbientOf
import androidx.ui.core.DensityAmbient
import androidx.ui.core.RepaintBoundary
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.layout.Container
import androidx.ui.unit.Size
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import coil.ImageLoader
import coil.request.GetRequestBuilder
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.essentials.ui.coroutines.load
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.injekt.inject

@Composable
fun CoilImage(
    data: Any,
    tintColor: Color? = null,
    size: Size? = null
) {
    val image = loadImage(data = data, size = size)
    val density = DensityAmbient.current
    RepaintBoundary {
        Container(
            width = size?.width ?: with(density) { image?.width?.ipx?.toDp() } ?: 0.dp,
            height = size?.height ?: with(density) { image?.height?.ipx?.toDp() } ?: 0.dp
        ) {
            Text("TODO")
        }
    }
}

@Composable
fun loadImage(
    data: Any,
    placeholder: ImageAsset? = PlaceholderAmbient.currentOrNull,
    size: Size? = null,
    imageLoader: ImageLoader = inject()
): ImageAsset? {
    return if (placeholder != null) {
        loadImage(data = data, placeholder = placeholder, size = size, imageLoader = imageLoader)
    } else {
        loadImage(data = data, imageLoader = imageLoader, size = size)
    }
}

@Composable
fun loadImage(
    data: Any,
    placeholder: ImageAsset,
    size: Size? = null,
    imageLoader: ImageLoader = inject()
): ImageAsset = loadImage(data = data, size = size, imageLoader = imageLoader) ?: placeholder

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
): ImageAsset? {
    val density = DensityAmbient.current
    return load(placeholder = null, key = listOf(data, size, imageLoader)) {
        imageLoader.getAnyNoInline(data) {
            if (size != null) {
                with(density) {
                    size(
                        width = size.width.toIntPx().value,
                        height = size.height.toIntPx().value
                    )
                }
            }
        }.toImageAsset()
    }
}

val PlaceholderAmbient = staticAmbientOf<ImageAsset>()

@Composable
fun CoilImage(
    data: Any,
    size: Size? = null,
    placeholder: ImageAsset? = PlaceholderAmbient.currentOrNull,
    imageLoader: ImageLoader = inject(),
    image: @Composable (ImageAsset?) -> Unit = {
        if (it != null) CoilImage(data, size, placeholder, imageLoader)
    }
) {
    RepaintBoundary {
        val loadedImage = loadImage(
            placeholder = placeholder,
            data = data,
            size = size,
            imageLoader = imageLoader
        )

        val density = DensityAmbient.current
        Container(
            width = size?.width ?: with(density) { loadedImage?.width?.ipx?.toDp() } ?: 0.dp,
            height = size?.height ?: with(density) { loadedImage?.height?.ipx?.toDp() } ?: 0.dp
        ) {
            if (loadedImage != null) {
                RepaintBoundary {
                    image(loadedImage)
                }
            }
        }
    }
}

@Composable
fun CoilImage(
    data: Any,
    size: Size? = null,
    placeholder: ImageAsset,
    imageLoader: ImageLoader = inject(),
    image: @Composable (ImageAsset) -> Unit = {
        CoilImage(data, size, placeholder, imageLoader)
    }
) {
    RepaintBoundary {
        val loadedImage = loadImage(
            placeholder = placeholder,
            data = data,
            size = size,
            imageLoader = imageLoader
        )

        val density = DensityAmbient.current
        Container(
            width = size?.width ?: with(density) { loadedImage.width.ipx.toDp() } ?: 0.dp,
            height = size?.height ?: with(density) { loadedImage.height.ipx.toDp() } ?: 0.dp
        ) {
            RepaintBoundary {
                image(loadedImage)
            }
        }
    }
}
