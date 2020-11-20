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

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

suspend fun <T, S> Flow<T>.reduceCollect(
    initial: S,
    reducer: S.(T) -> S
) {
    var state = initial
    collect { state = state.reducer(it) }
}

suspend fun <T, S> Flow<T>.reduceCollectIn(
    scope: CoroutineScope,
    initial: S,
    reducer: S.(T) -> S
) = scope.launch {
    reduceCollect(initial, reducer)
}
