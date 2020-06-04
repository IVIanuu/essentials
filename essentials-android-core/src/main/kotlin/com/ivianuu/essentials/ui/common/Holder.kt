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
import androidx.compose.ReferentiallyEqual
import androidx.compose.Stable
import androidx.compose.remember

@Composable
inline fun <T> holder(
    noinline areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual,
    crossinline init: () -> T
): MutableHolder<T> = remember { mutableHolderOf(init(), areEquivalent) }

@Composable
inline fun <T, V1> holderFor(
    v1: V1,
    noinline areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual,
    crossinline init: () -> T
): MutableHolder<T> = remember(v1) { mutableHolderOf(init(), areEquivalent) }

@Composable
inline fun <T, V1, V2> holderFor(
    v1: V1,
    v2: V2,
    noinline areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual,
    crossinline init: () -> T
): MutableHolder<T> = remember(v1, v2) { mutableHolderOf(init(), areEquivalent) }

@Composable
inline fun <T> holderFor(
    vararg inputs: Any?,
    noinline areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual,
    crossinline init: () -> T
): MutableHolder<T> = remember(*inputs) {
    mutableHolderOf(init(), areEquivalent)
}

fun <T> mutableHolderOf(
    value: T,
    areEquivalent: (old: T, new: T) -> Boolean = ReferentiallyEqual
): MutableHolder<T> = MutableHolderImpl(value, areEquivalent)

interface Holder<T> {
    val value: T
}

interface MutableHolder<T> : Holder<T> {
    override var value: T
}

@Stable
private class MutableHolderImpl<T> internal constructor(
    value: T,
    val areEquivalent: (old: T, new: T) -> Boolean
) : MutableHolder<T> {
    override var value: T = value
        set(value) {
            if (!areEquivalent(field, value)) {
                field = value
            }
        }
}
