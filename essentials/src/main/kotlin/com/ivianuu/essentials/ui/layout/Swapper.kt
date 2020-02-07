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
import androidx.compose.Model
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.ui.core.Alignment
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.layout.Container
import androidx.ui.unit.PxPosition

@Composable
fun <T> Swapper(
    state: SwapperState<T>,
    modifier: Modifier = Modifier.None,
    child: @Composable (T) -> Unit
) {
    val children: @Composable () -> Unit = {
        state.keepStateItems.forEach { item ->
            key(item as Any) {
                child(item)
            }
        }

        key(state.current as Any) {
            Container(alignment = Alignment.TopLeft) {
                child(state.current)
            }
        }
    }

    Layout(children = children, modifier = modifier) { measurables, constraints ->
        val placeable = measurables.last().measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(PxPosition.Origin)
        }
    }
}

@Model
class SwapperState<T>(
    initial: T,
    keepState: Boolean = false
) {

    private var _keepState = keepState
    var keepState: Boolean
        get() = _keepState
        set(value) {
            if (!value) _keepStateItems.clear()
            _keepState = value
        }

    private var _current = initial
    var current: T
        get() = _current
        set(value) {
            if (value != _current) {
                if (keepState) _keepStateItems += _current
                _current = value
                if (_current in keepStateItems) _keepStateItems -= _current
            }
        }

    val keepStateItems: List<T> get() = _keepStateItems
    private val _keepStateItems = modelListOf<T>()
}
