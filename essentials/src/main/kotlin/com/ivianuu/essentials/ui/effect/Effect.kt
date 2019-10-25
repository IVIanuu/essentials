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

package com.ivianuu.essentials.ui.effect

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.reflect.KProperty

interface EffectContext {
    fun <T> cache(key: Any, inputs: Array<out Any?>?, block: () -> T): T

    fun onDispose(callback: () -> Unit)

    fun dispose()
}

fun EffectContext(): EffectContext = EffectContextImpl()

inline fun <T> EffectContext.memo(
    key: Any = sourceLocation(),
    vararg inputs: Any?,
    crossinline calculation: () -> T
): T = cache(key, inputs) { calculation() }

private class EffectContextImpl : EffectContext {

    private class EffectState(
        val inputs: Array<out Any?>?,
        val value: Any?
    )

    private val effectStates = mutableMapOf<Any, EffectState>()

    private val onDisposeCallbacks = mutableListOf<() -> Unit>()

    override fun <T> cache(key: Any, inputs: Array<out Any?>?, block: () -> T): T {
        val state = effectStates[key]

        return if (state != null && ((inputs != null && state.inputs != null && inputs.contentEquals(
                state.inputs
            )) || (inputs == null && state.inputs == null))
        ) {
            state.value as T
        } else {
            val newValue = block()
            effectStates[key] = EffectState(inputs, newValue)
            newValue
        }
    }

    override fun onDispose(callback: () -> Unit) {
        onDisposeCallbacks.add(callback)
    }

    override fun dispose() {
        effectStates.clear()
        onDisposeCallbacks.toList().forEach { it() }
    }
}

inline fun EffectContext.onActive(
    key: Any = sourceLocation(),
    crossinline callback: () -> Unit
) {
    memo(key) { callback() }
}

class State<T> @PublishedApi internal constructor(var value: T) {
    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }
}

inline fun <T> EffectContext.state(
    key: Any = sourceLocation(),
    vararg inputs: Any?,
    crossinline init: () -> T
) = memo(key = key, inputs = *inputs, calculation = { State(init()) })

fun EffectContext.coroutineScope(): CoroutineScope {
    val coroutineScope = memo { CoroutineScope(Dispatchers.Main + Job()) }
    onDispose { coroutineScope.coroutineContext[Job.Key]?.cancel() }
    return coroutineScope
}

inline fun sourceLocation(): String {
    val element = Throwable().stackTrace.first()
    return "${element.className}:${element.methodName}:${element.lineNumber}"
}