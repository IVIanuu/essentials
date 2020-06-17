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
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.Image
import androidx.ui.graphics.painter.ImagePainter
import coil.Coil
import coil.request.GetRequest
import coil.request.GetRequestBuilder
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.common.AsyncBox
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.launchAsync

@Composable
fun CoilImage(
    data: Any,
    modifier: Modifier = Modifier,
    transition: StackTransition = FadeStackTransition(),
    builderBlock: (GetRequestBuilder.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    error: @Composable (() -> Unit)? = null
) {
    WithConstraints(modifier = modifier) {
        val width = if (constraints.maxWidth != Int.MAX_VALUE) constraints.maxWidth else null
        val height = if (constraints.maxHeight != Int.MAX_VALUE) constraints.maxHeight else null
        val context = ContextAmbient.current

        val state = launchAsync(
            data,
            builderBlock,
            width,
            height
        ) {
            Coil.imageLoader(context).execute(
                GetRequest.Builder(context)
                    .apply {
                        data(data)
                        if (width != null && height != null) {
                            size(width, height)
                        }
                        builderBlock?.invoke(this)
                    }
                    .build())
                .drawable!!
                .toImageAsset()
                .let { ImagePainter(it) }
        }

        AsyncBox(
            state = state,
            transition = transition,
            fail = { error?.invoke() },
            loading = { placeholder?.invoke() },
            success = { Image(it) }
        )
    }
}
