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

package com.ivianuu.essentials.ui.compose.box

import androidx.compose.Composable
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun <T> unfoldBox(box: Box<T>): BoxWrapper<T> = effect {
    val coroutineScope = coroutineScope()
    val wrapper = remember {
        BoxWrapper(value = box.defaultValue) { newValue ->
            coroutineScope.launch {
                box.set(newValue)
            }
        }
    }
    wrapper.value = collect(box.asFlow(), box.defaultValue)
    return@effect wrapper
}

class BoxWrapper<T> internal constructor(
    value: T,
    private val setter: (T) -> Unit
) {

    internal var _internalValue = value

    var value: T
        get() = _internalValue
        set(value) {
            setter(value)
        }

    operator fun component1(): T = value
    operator fun component2(): (T) -> Unit = { value = it }
}