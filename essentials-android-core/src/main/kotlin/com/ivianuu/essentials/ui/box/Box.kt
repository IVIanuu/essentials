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

package com.ivianuu.essentials.ui.box

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.remember
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.coroutines.collectAsState
import com.ivianuu.essentials.ui.coroutines.compositionCoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

@Composable
fun <T> boxState(box: Box<T>): BoxState<T> {
    val coroutineScope = compositionCoroutineScope()
    val wrapper = remember {
        val setter: (T) -> Unit = { newValue ->
            coroutineScope.launch {
                box.updateData { newValue }
            }
        }

        BoxState(data = box.defaultData, setter = setter)
    }
    wrapper.internalData = box.data.collectAsState(box.defaultData).value
    return wrapper
}

@Stable
class BoxState<T> internal constructor(
    data: T,
    private val setter: (T) -> Unit
) {

    internal var internalData = data

    var data: T
        get() = internalData
        set(value) {
            setter(value)
        }

    operator fun component1(): T = data
    operator fun component2(): (T) -> Unit = { data = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = data

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        data = next
    }
}
