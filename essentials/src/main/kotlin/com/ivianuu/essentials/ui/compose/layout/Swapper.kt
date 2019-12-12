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

package com.ivianuu.essentials.ui.compose.layout

import androidx.compose.Composable
import androidx.compose.frames.modelListOf
import androidx.ui.core.Layout
import androidx.ui.core.PxPosition
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.key

fun <T> Swapper(
    controller: SwapperController<T>,
    child: (T) -> Unit
) {
    val children: @Composable() () -> Unit = {
        key(controller.current as Any) {
            child(controller.current)
        }

        controller.keepStateItems.forEach { item ->
            key(item as Any) {
                child(item)
            }
        }
    }

    Layout(children = children) { measureables, constraints ->
        val placeable = measureables.first().measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(PxPosition.Origin)
        }
    }
}

class SwapperController<T>(
    initial: T,
    keepState: Boolean = false
) {

    private var _keepState by framed(keepState)
    var keepState: Boolean
        get() = _keepState
        set(value) {
            if (!value) _keepStateItems.clear()
            _keepState = value
        }

    private var _current by framed(initial)
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
