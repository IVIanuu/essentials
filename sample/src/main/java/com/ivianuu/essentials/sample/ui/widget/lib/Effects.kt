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

package com.ivianuu.essentials.sample.ui.widget.lib

import com.ivianuu.essentials.util.cast
import kotlin.reflect.KProperty

internal data class EffectState<T>(
    val inputs: List<Any?>?,
    val result: T
)

class Effect<T> internal constructor(
    @PublishedApi
    internal val block: Effect<T>.() -> T
) {

    lateinit var context: BuildContext

    fun resolve(context: BuildContext): T {
        this.context = context
        return block()
    }

    operator fun invoke(context: BuildContext) = resolve(context)

}

fun <T> effectOf(block: Effect<T>.() -> T): Effect<T> =
    Effect(block)

fun <T> memo(calculation: () -> T) = effectOf<T> {
    context.cache(calculation)
}

fun <T> memo(vararg inputs: Any?, calculation: () -> T) =
    effectOf<T> { context.cache(*inputs) { calculation() } }

class State<T> @PublishedApi internal constructor(
    value: T,
    private val onChange: (T) -> Unit
) {

    var value: T = value
        set(value) {
            field = value
            onChange(value)
        }

    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }
}

inline fun <T> state(crossinline init: () -> T) = effectOf<State<T>> {
    context.cache {
        State(init()) {
            invalidate().invoke(context)()
        }
    }
}

inline fun <T> state(vararg inputs: Any?, crossinline init: () -> T) = effectOf<State<T>> {
    context.cache(*inputs) {
        State(init()) {
            invalidate().invoke(context)()
        }
    }
}

fun invalidate() = effectOf<() -> Unit> {
    return@effectOf { context.cast<Element>().markNeedsBuild() }
}