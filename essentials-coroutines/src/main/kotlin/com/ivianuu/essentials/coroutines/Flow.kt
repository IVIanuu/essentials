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

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow

// todo better name
fun <T> flowNever(): Flow<T> = flow {
    while (true) {
        delay(Long.MAX_VALUE)
    }
}

fun <T> flowOf(block: suspend () -> T): Flow<T> = flow { emit(block()) }

fun <T1, T2> combine(flow: Flow<T1>, flow2: Flow<T2>): Flow<Pair<T1, T2>> {
    return combine(
        flow = flow,
        flow2 = flow2,
        transform = { item, item2 -> Pair(item, item2) }
    )
}

fun <T1, T2, T3> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>
): Flow<Triple<T1, T2, T3>> {
    return combine(
        flow = flow,
        flow2 = flow2,
        flow3 = flow3,
        transform = { item, item2, item3 -> Triple(item, item2, item3) }
    )
}

fun <T> merge(flows: List<Flow<T>>): Flow<T> {
    return if (flows.isEmpty()) emptyFlow()
    else flows.asFlow().flattenMerge(concurrency = flows.size)
}
