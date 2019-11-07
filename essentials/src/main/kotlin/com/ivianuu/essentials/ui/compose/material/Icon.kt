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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.ui.core.Size
import androidx.ui.core.dp
import androidx.ui.foundation.DrawImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.material.textColorForBackground
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun Icon(
    image: Image,
    style: IconStyle = +ambient(CurrentIconStyleAmbient),
    size: Size = style.size,
    tint: Color? = style.tint
) = composable("Icon") {
    Container(
        width = size.width,
        height = size.height
    ) {
        DrawImage(image = image, tint = tint)
    }
}

data class IconStyle(
    val size: Size = Size(DefaultIconSize, DefaultIconSize),
    val tint: Color? = null
)

val CurrentIconStyleAmbient = Ambient.of { IconStyle() }

fun <T> iconStyleValue(
    choosingBlock: IconStyle.() -> T
) = effectOf<T> {
    (+ambient(CurrentIconStyleAmbient)).choosingBlock()
}

private val DefaultIconSize = 24.dp

fun AvatarIconStyle() = IconStyle(size = Size(AvatarSize, AvatarSize), tint = null)

private val AvatarSize = 40.dp

fun iconColorForBackground(background: Color) = textColorForBackground(background)