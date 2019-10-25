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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun ImageButton(
    image: Image,
    tint: Color? = null,
    width: Dp = DefaultImageButtonSize,
    height: Dp = DefaultImageButtonSize,
    onClick: (() -> Unit)? = null
) = composable("ImageButton") {
    Ripple(bounded = false, enabled = onClick != null) {
        Clickable(onClick = onClick) {
            Container(width = width, height = height) {
                SimpleImage(image, tint)
            }
        }
    }
}

private val DefaultImageButtonSize = 40.dp