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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.ui.core.Modifier
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import coil.ImageLoader
import coil.api.getAny
import com.ivianuu.essentials.ui.core.call
import com.ivianuu.essentials.ui.coroutines.load
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.essentials.ui.injekt.inject

@Composable
fun image(
    data: Any,
    placeholder: Image? = ambient(PlaceholderAmbient),
    imageLoader: ImageLoader = inject()
): Image? {
    return if (placeholder != null) {
        image(data = data, placeholder = placeholder, imageLoader = imageLoader)
    } else {
        image(data = data, imageLoader = imageLoader)
    }
}

@JvmName("imageNonNullPlaceholder")
@Composable
fun image(
    data: Any,
    placeholder: Image,
    imageLoader: ImageLoader = inject()
): Image = image(data = data, imageLoader = imageLoader) ?: placeholder

@Composable
fun image(
    data: Any,
    imageLoader: ImageLoader = inject()
): Image? {
    return load(placeholder = null, key = data to imageLoader) {
        imageLoader.getAny(data).toImage()
    }
}

val PlaceholderAmbient = Ambient.of<Image?>()
// todo make non null once we have something like ambientOrNull or ambientOrDefault

@Composable
fun Image(
    data: Any,
    modifier: Modifier = Modifier.None,
    placeholder: Image? = ambient(PlaceholderAmbient),
    imageLoader: ImageLoader = inject(),
    image: @Composable() (Image?) -> Unit
) {
    call(data, modifier, placeholder, imageLoader, image) {
        val loadedImage = image(
            placeholder = placeholder,
            data = data,
            imageLoader = imageLoader
        )

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
    image: @Composable() (Image) -> Unit
) {
    call(data, modifier, placeholder, imageLoader, image) {
        val loadedImage = image(
            placeholder = placeholder,
            data = data,
            imageLoader = imageLoader
        )

        Container(modifier = modifier) {
            image(loadedImage)
        }
    }
}
