/*
 * Copyright 2020 Manuel Wrage
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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.DisposableHandle

@Suppress("UNCHECKED_CAST")
class RetainedObjects {
    private var isDisposed = false

    private val backing = mutableMapOf<Any, Any?>()

    operator fun <T> get(key: Any): T {
        checkNotDisposed()
        return backing[key] as T
    }

    fun <T> getOrNull(key: Any): T? {
        checkNotDisposed()
        return backing[key] as? T
    }

    operator fun <T> set(key: Any, value: T) {
        checkNotDisposed()
        remove(key)
        backing[key] = value
    }

    operator fun contains(key: Any): Boolean {
        checkNotDisposed()
        return backing.containsKey(key)
    }

    fun remove(key: Any) {
        checkNotDisposed()
        val value = backing.remove(key)
        (value as? DisposableHandle)?.dispose()
    }

    fun dispose() {
        backing.keys.toList().forEach { remove(it) }
        isDisposed = true
    }

    private fun checkNotDisposed() {
        check(!isDisposed) {
            "RetainedObjects instance is disposed"
        }
    }
}

val LocalRetainedObjects = staticCompositionLocalOf<RetainedObjects> {
    error("No RetainedObjects specified")
}

// todo remove once compose is fixed
@Composable
inline fun <T> rememberRetained(
    key: Any = currentComposer.compoundKeyHash,
    noinline init: () -> T
): T = rememberRetained(inputs = *emptyArray(), key = key, init = init)

@Composable
inline fun <T> rememberRetained(
    vararg inputs: Any?,
    key: Any = currentComposer.compoundKeyHash,
    noinline init: () -> T
): T {
    val retainedObjects = LocalRetainedObjects.current
    var value: ValueWithInputs<T>? = retainedObjects.getOrNull(key)
    if (value != null && !value.inputs.contentEquals(inputs)) {
        (value as? DisposableHandle)?.dispose()
        value = null
    }

    if (value == null) {
        value = ValueWithInputs(init(), inputs)
        retainedObjects[key] = value
    }

    return value.value
}

@PublishedApi
internal class ValueWithInputs<T>(
    val value: T,
    val inputs: Array<out Any?>
) : DisposableHandle {
    override fun dispose() {
        (value as? DisposableHandle)?.dispose()
    }
}
