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
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.WithConstraints
import androidx.ui.core.asModifier
import androidx.ui.foundation.Box
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.unit.isFinite
import coil.ImageLoader
import coil.request.GetRequestBuilder
import com.ivianuu.essentials.ui.coroutines.launch
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.injekt.inject

@Composable
fun CoilImage(
    data: Any,
    modifier: Modifier = Modifier.None,
    builderBlock: (GetRequestBuilder.() -> Unit)? = null,
    imageLoader: ImageLoader = inject()
) {
    WithConstraints(modifier = modifier) { constraints ->
        val width = if (constraints.maxWidth.isFinite()) constraints.maxWidth else null
        val height = if (constraints.maxHeight.isFinite()) constraints.maxHeight else null

        val image = launch(data, builderBlock, imageLoader, width, height) {
            imageLoader.get(
                GetRequestBuilder(imageLoader.defaults)
                    .apply {
                        data(data)

                        if (width != null && height != null) {
                            size(width.value, height.value)
                        }
                        builderBlock?.invoke(this)
                    }
                    .build()
            ).toImageAsset()
        }

        val painter = remember(image) { image?.let { ImagePainter(it) } }
        if (painter != null) {
            RepaintBoundary {
                Box(modifier = painter.asModifier())
            }
        }
    }
}
