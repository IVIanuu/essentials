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

package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.EdgeInsets
import androidx.ui.unit.ipx
import androidx.ui.unit.max
import androidx.ui.unit.min
import com.ivianuu.essentials.ui.core.looseMin

// todo remove

@Composable
fun AddPaddingIfNeededLayout(
    padding: EdgeInsets,
    modifier: Modifier = Modifier.None,
    child: @Composable () -> Unit
) {
    SingleChildLayout(child = child, modifier = modifier) { measureable, constraints ->
        if (measureable == null) return@SingleChildLayout layout(0.ipx, 0.ipx) {}

        val placeable = measureable.measure(constraints.looseMin())
        val width = max(
            constraints.minWidth,
            placeable.width + padding.left.toIntPx() + padding.right.toIntPx()
        )
        val height = max(
            constraints.minHeight,
            placeable.height + padding.top.toIntPx() + padding.bottom.toIntPx()
        )

        layout(width, height) {
            placeable.place(
                x = min(width / 2, padding.left.toIntPx()),
                y = min(height / 2, padding.top.toIntPx())
            )
        }
    }
}
