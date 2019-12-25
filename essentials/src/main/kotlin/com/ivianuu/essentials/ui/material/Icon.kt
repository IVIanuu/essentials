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

package com.ivianuu.essentials.ui.material

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.ui.core.Size
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.material.ripple.Ripple

@Immutable
data class IconStyle(
    val size: Size = Size(24.dp, 24.dp),
    val tint: Boolean = true
)

val IconStyleAmbient = Ambient.of { IconStyle() }

@Composable
fun AvatarIconStyle() = IconStyle(
    size = Size(40.dp, 40.dp),
    tint = false
)

@Composable
fun Icon(
    image: Image,
    style: IconStyle = ambient(IconStyleAmbient)
) {
    Container(
        width = style.size.width,
        height = style.size.height
    ) {
        val color = if (style.tint) contentColor() else null
        DrawImage(image = image, tint = color)
    }
}

@Composable
fun IconButton(
    image: Image,
    onClick: (() -> Unit)? = null
) {
    IconButton(onClick = onClick) {
        Icon(image = image)
    }
}

@Composable
fun IconButton(
    onClick: (() -> Unit)? = null,
    icon: @Composable() () -> Unit
) {
    Ripple(
        bounded = false,
        enabled = onClick != null
    ) {
        Clickable(onClick = onClick) {
            Container {
                icon()
            }
        }
    }
}
