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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambientOf
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Shape
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.material.Surface

// todo rename to dialog frame

@Immutable
data class DialogStyle(
    val shape: Shape = RoundedCornerShape(size = 4.dp)
)

val DialogStyleAmbient = ambientOf { DialogStyle() }

@Composable
fun Dialog(
    style: DialogStyle = DialogStyleAmbient.current,
    children: @Composable () -> Unit
) {
    Surface(
        modifier = LayoutPadding(
            left = 32.dp,
            top = 32.dp,
            right = 32.dp,
            bottom = 32.dp
        ) + LayoutWidth.Constrain(minWidth = 280.dp, maxWidth = 356.dp),
        color = MaterialTheme.colors().surface,
        elevation = 24.dp,
        shape = style.shape,
        children = children
    )
}
