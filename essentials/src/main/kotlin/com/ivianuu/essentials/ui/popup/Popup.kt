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

package com.ivianuu.essentials.ui.popup

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Shape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.LayoutPadding
import com.ivianuu.essentials.ui.material.EsSurface

data class PopupStyle(
    val shape: Shape = RoundedCornerShape(size = 4.dp)
)

val PopupStyleAmbient = Ambient.of { PopupStyle() }

@Composable
fun Popup(
    style: PopupStyle = ambient(PopupStyleAmbient),
    children: @Composable() () -> Unit
) {
    EsSurface(
        elevation = 8.dp,
        modifier = LayoutPadding(top = 8.dp, bottom = 8.dp),
        shape = style.shape,
        children = children
    )
}
