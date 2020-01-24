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
import androidx.ui.core.Modifier
import androidx.ui.core.RepaintBoundary
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import coil.ImageLoader
import coil.request.GetRequestBuilder
import com.ivianuu.essentials.ui.core.ambientOf
import com.ivianuu.essentials.ui.core.current
import com.ivianuu.essentials.ui.coroutines.load
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.AvatarIconStyle
import com.ivianuu.essentials.ui.material.Icon

@Composable
fun loadImage(
    data: Any,
    placeholder: Image? = PlaceholderAmbient.current,
    imageLoader: ImageLoader = inject()
): Image? {
    return if (placeholder != null) {
        loadImage(data = data, placeholder = placeholder, imageLoader = imageLoader)
    } else {
        loadImage(data = data, imageLoader = imageLoader)
    }
}

@JvmName("imageNonNullPlaceholder")
@Composable
fun loadImage(
    data: Any,
    placeholder: Image,
    imageLoader: ImageLoader = inject()
): Image = loadImage(data = data, imageLoader = imageLoader) ?: placeholder

// todo ir
suspend fun ImageLoader.getAnyNoInline(
    data: Any,
    builder: GetRequestBuilder.() -> Unit = {}
): Drawable = get(GetRequestBuilder(defaults).data(data).apply(builder).build())

@Composable
fun loadImage(
    data: Any,
    imageLoader: ImageLoader = inject()
): Image? {
    return load(placeholder = null, key = data to imageLoader) {
        imageLoader.getAnyNoInline(data).toImage()
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
        if (it != null) Icon(image = it, style = AvatarIconStyle())
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
        Icon(image = it, style = AvatarIconStyle())
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
