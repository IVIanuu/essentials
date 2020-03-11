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

package com.ivianuu.essentials.android.ui.box

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.remember
import com.ivianuu.essentials.android.ui.coroutines.CoroutineScopeAmbient
import com.ivianuu.essentials.android.ui.coroutines.collect
import com.ivianuu.essentials.store.Box
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

@Composable
fun <T> unfoldBox(box: Box<T>): BoxWrapper<T> {
    val coroutineScope = CoroutineScopeAmbient.current
    val wrapper = remember {
        val setter: (T) -> Unit = { newValue ->
            coroutineScope.launch {
                box.set(newValue)
            }
        }

        return@remember BoxWrapper(value = box.defaultValue, setter = setter)
    }
    wrapper.internalValue = collect(box.asFlow(), box.defaultValue)
    return wrapper
}

@Stable
class BoxWrapper<T> internal constructor(
    value: T,
    private val setter: (T) -> Unit
) {

    internal var internalValue = value

    var value: T
        get() = internalValue
        set(value) {
            setter(value)
        }

    operator fun component1(): T = value
    operator fun component2(): (T) -> Unit = { value = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }
}
