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

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.ambient
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Shape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.LayoutMaxWidth
import androidx.ui.layout.LayoutMinWidth
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.layout.WithModifier
import com.ivianuu.essentials.ui.material.Surface

@Immutable
data class DialogStyle(
    val shape: Shape = RoundedCornerShape(size = 4.dp)
)

val DialogStyleAmbient = Ambient.of { DialogStyle() }

@Composable
fun Dialog(
    style: DialogStyle = ambient(DialogStyleAmbient),
    child: @Composable() () -> Unit
) {
    WithModifier(
        modifier = LayoutPadding(
            left = 32.dp,
            top = 32.dp,
            right = 32.dp,
            bottom = 32.dp
        ) + LayoutMinWidth(280.dp) + LayoutMaxWidth(356.dp)
    ) {
        Surface(
            color = MaterialTheme.colors().surface,
            elevation = 24.dp,
            shape = style.shape,
            children = child
        )
    }
}
