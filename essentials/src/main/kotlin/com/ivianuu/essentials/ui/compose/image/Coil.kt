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

package com.ivianuu.essentials.ui.compose.image

import androidx.compose.Composable
import androidx.ui.graphics.Image
import coil.ImageLoader
import coil.api.getAny
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.essentials.ui.compose.coroutines.load
import com.ivianuu.essentials.ui.compose.injekt.inject

@Composable
fun loadCoilImageAny(
    placeholder: Image,
    data: Any
): Image = effect {
    val imageLoader = inject<ImageLoader>()
    return@effect load(placeholder = placeholder) {
        return@load imageLoader.getAny(data).toImage()
    }
}

@Composable
fun CoilImageAny(
    data: Any,
    placeholder: Image? = null,
    image: @Composable() (Image) -> Unit
) = composableWithKey(data) {
    val wasPlaceholderNull = placeholder == null
    // todo better default placeholder
    val placeholder = memo(placeholder) { placeholder ?: Image(1, 1) }
    val loadedImage = loadCoilImageAny(
        placeholder = placeholder,
        data = data
    )

    if (!wasPlaceholderNull || loadedImage != placeholder) {
        image.invokeAsComposable(loadedImage)
    }
}