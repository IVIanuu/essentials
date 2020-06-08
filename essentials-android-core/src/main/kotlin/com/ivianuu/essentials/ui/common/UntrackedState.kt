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

package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.ReferentiallyEqual
import androidx.compose.remember

@Composable
inline fun <T> untrackedState(
    noinline areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual,
    crossinline init: () -> T
): MutableState<T> = remember { untrackedStateOf(init(), areEquivalent) }

@Composable
inline fun <T> untrackedStateFor(
    vararg inputs: Any?,
    noinline areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual,
    crossinline init: () -> T
): MutableState<T> = remember(*inputs) {
    untrackedStateOf(init(), areEquivalent)
}

fun <T> untrackedStateOf(
    value: T,
    areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual
): MutableState<T> = UntrackedState(value, areEquivalent)

private class UntrackedState<T>(
    value: T,
    val areEquivalent: (old: T, new: T) -> Boolean
) : MutableState<T> {
    override var value: T = value
        get() = synchronized(this) { field }
        set(value) {
            val oldValue = synchronized(this) { field }
            if (!areEquivalent(oldValue, value)) {
                synchronized(this) { field = value }
            }
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}
