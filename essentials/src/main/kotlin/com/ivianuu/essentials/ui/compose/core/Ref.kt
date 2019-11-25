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

package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Composable

@Composable
fun <T> ref(init: () -> T) = effect {
    memo { Ref(init()) }
}

@Composable
fun <T, V1> refFor(v1: V1, init: () -> T) = effect {
    memo(v1) { Ref(init()) }
}

@Composable
fun <T, V1, V2> refFor(
    v1: V1,
    v2: V2,
    init: () -> T
) = effect {
    memo(v1, v2) { Ref(init()) }
}

@Composable
fun <T> refFor(vararg inputs: Any?, init: () -> T) = effect {
    memo(*inputs) { Ref(init()) }
}

data class Ref<T>(var value: T)