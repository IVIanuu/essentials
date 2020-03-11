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
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.asModifier
import androidx.ui.core.drawLayer
import androidx.ui.foundation.Box
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.unit.isFinite
import coil.ImageLoader
import coil.request.GetRequestBuilder
import com.ivianuu.essentials.android.ui.common.RenderAsync
import com.ivianuu.essentials.android.ui.image.toImageAsset
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.android.util.launchAsync

@Composable
fun CoilImage(
    data: Any,
    modifier: Modifier = Modifier.None,
    builderBlock: (GetRequestBuilder.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null,
    imageLoader: ImageLoader = inject()
) {
    WithConstraints(modifier = modifier) { constraints, _ ->
        val width = if (constraints.maxWidth.isFinite()) constraints.maxWidth else null
        val height = if (constraints.maxHeight.isFinite()) constraints.maxHeight else null

        val state = launchAsync(data, builderBlock, imageLoader, width, height) {
            imageLoader.get(
                GetRequestBuilder(imageLoader.defaults)
                    .apply {
                        data(data)
                        if (width != null && height != null) {
                            size(width.value, height.value)
                        }
                        builderBlock?.invoke(this)
                    }
                    .build())
                .toImageAsset()
                .let { ImagePainter(it) }
        }

        RenderAsync(
            state = state,
            fail = { error?.invoke() },
            loading = { placeholder?.invoke() },
            success = {
                Box(modifier = drawLayer() + it.asModifier())
            }
        )
    }
}
