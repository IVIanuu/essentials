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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.WithDensity
import androidx.ui.core.dp
import com.ivianuu.essentials.ui.compose.core.Axis
import com.ivianuu.essentials.ui.compose.layout.Offset

@Composable
fun Scroller(
    position: ScrollPosition = +memo { ScrollPosition() },
    direction: Axis = Axis.Vertical,
    enabled: Boolean = true,
    reverse: Boolean = false,
    child: @Composable() () -> Unit
) {
    Scrollable(
        position = position,
        direction = direction,
        reverse = reverse,
        enabled = enabled
    ) {
        WithDensity {
            val offsetX: Dp
            val offsetY: Dp

            when (direction) {
                Axis.Vertical -> {
                    offsetX = 0.dp
                    offsetY = position.value.toDp()
                }
                Axis.Horizontal -> {
                    offsetX = position.value.toDp()
                    offsetY = 0.dp
                }
            }

            Offset(
                offsetX = offsetX,
                offsetY = offsetY,
                child = child
            )
        }
    }
}