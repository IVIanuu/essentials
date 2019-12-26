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

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import coil.ImageLoader
import coil.api.getAny
import com.ivianuu.essentials.ui.coroutines.load
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.essentials.ui.injekt.inject

@Composable
fun image(
    data: Any,
    placeholder: Image,
    imageLoader: ImageLoader = inject()
): Image = image(data, imageLoader) ?: placeholder

@Composable
fun image(
    data: Any,
    imageLoader: ImageLoader = inject()
): Image? {
    return load(placeholder = null, key = data to imageLoader) {
        imageLoader.getAny(data).toImage()
    }
}

@Composable
fun Image(
    data: Any,
    modifier: Modifier = Modifier.None,
    placeholder: Image? = null,
    imageLoader: ImageLoader = inject(),
    image: @Composable() (Image) -> Unit
) {
    val wasPlaceholderNull = placeholder == null
    // todo better default placeholder
    val placeholder = remember(placeholder) {
        placeholder ?: Image(1, 1)
    }
    val loadedImage = image(
        placeholder = placeholder,
        data = data,
        imageLoader = imageLoader
    )

    Container(modifier = modifier) {
        if (!wasPlaceholderNull || loadedImage != placeholder) {
            image(loadedImage)
        }
    }
}
