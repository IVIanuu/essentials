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

package com.ivianuu.essentials.ui.core

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.Stable
import androidx.compose.mutableStateOf
import com.ivianuu.essentials.composehelpers.sourceLocation
import java.io.Closeable

@Stable
class RetainedObjects {
    private val backing = mutableMapOf<Any, Any?>()

    operator fun <T> get(key: Any): T? = backing[key] as? T

    operator fun <T> set(key: Any, value: T) {
        remove(key)
        backing[key] = value
    }

    fun contains(key: Any): Boolean = backing.containsKey(key)

    fun remove(key: Any) {
        val value = backing.remove(key)
        (value as? Closeable)?.close()
    }

    fun dispose() {
        backing.keys.toList().forEach { remove(it) }
    }
}

fun <T> RetainedObjects.getOrSet(key: Any, defaultValue: () -> T): T {
    var value = get<T>(key)
    if (value == null) {
        value = defaultValue()
        set(key, value)
    }

    return value as T
}

fun <T> RetainedObjects.getOrDefault(key: Any, defaultValue: () -> T): T {
    var value = get<T>(key)
    if (value == null) {
        value = defaultValue()
    }

    return value as T
}

private class ValueWithInputs<T>(
    val value: T,
    val inputs: Array<out Any?>
)

fun <T> RetainedObjects.getOrSetIfChanged(
    key: Any,
    vararg inputs: Any?,
    defaultValue: () -> T
): T {
    var value: ValueWithInputs<T>? = get<ValueWithInputs<T>>(key)
    if (value != null && !value.inputs.contentEquals(inputs)) {
        value = null
    }

    if (value == null) {
        value = ValueWithInputs(
            defaultValue(),
            inputs
        )
        set(key, value)
    }

    return value.value
}

val RetainedObjectsAmbient = ambientOf<RetainedObjects> { error("No retained objects provided") }

@Composable
inline fun <T> retain(noinline init: () -> T): T =
    retain(
        key = sourceLocation(),
        init = init
    )

@Composable
fun <T> retain(
    key: Any,
    init: () -> T
): T {
    val retainedObjects = RetainedObjectsAmbient.current
    return retainedObjects.getOrSet(key, init)
}

@Composable
inline fun <T> retainFor(
    vararg inputs: Any?,
    noinline init: () -> T
): T = retainFor(
    key = sourceLocation(),
    inputs = *inputs,
    init = init
)

@Composable
fun <T> retainFor(
    key: Any,
    vararg inputs: Any?,
    init: () -> T
): T {
    val retainedObjects = RetainedObjectsAmbient.current
    return retainedObjects.getOrSetIfChanged(key, *inputs, defaultValue = init)
}

@Composable
inline fun <T> retainedState(
    noinline init: () -> T
): MutableState<T> = retainedState(
    key = sourceLocation(),
    init = init
)

@Composable
fun <T> retainedState(
    key: Any,
    init: () -> T
): MutableState<T> =
    retain(key) { mutableStateOf(init()) }

@Composable
inline fun <T> retainedStateFor(
    vararg inputs: Any?,
    noinline init: () -> T
): MutableState<T> = retainedStateFor(
    key = sourceLocation(),
    inputs = *inputs,
    init = init
)

@Composable
fun <T> retainedStateFor(
    key: Any,
    vararg inputs: Any?,
    init: () -> T
): MutableState<T> = retainFor(
    key,
    *inputs
) { mutableStateOf(init()) }
