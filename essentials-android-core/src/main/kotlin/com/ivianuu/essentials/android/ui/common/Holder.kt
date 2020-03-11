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

package com.ivianuu.essentials.android.ui.common

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.remember

@Composable
inline fun <T> holder(crossinline init: () -> T): Holder<T> = remember { holderOf(init()) }

@Composable
inline fun <T, V1> holderFor(v1: V1, crossinline init: () -> T): Holder<T> = remember(v1) { holderOf(init()) }

@Composable
inline fun <T, V1, V2> holderFor(
    v1: V1,
    v2: V2,
    crossinline init: () -> T
): Holder<T> = remember(v1, v2) { holderOf(init()) }

@Composable
inline fun <T> holderFor(vararg inputs: Any?, crossinline init: () -> T): Holder<T> = remember(*inputs) {
    holderOf(init())
}

fun <T> holderOf(value: T) = Holder(value)

@Stable
data class Holder<T> internal constructor(var value: T)
