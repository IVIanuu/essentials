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

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map

interface StateFlow<T> : Flow<T> {
    val value: T
}

interface MutableStateFlow<T> : StateFlow<T> {
    override var value: T
}

fun <T> DataFlow(): MutableStateFlow<T> = DataFlowImpl(Null)

fun <T> DataFlow(initial: T): MutableStateFlow<T> = DataFlowImpl(initial)

private class DataFlowImpl<T>(
    initial: Any?
) : AbstractFlow<T>(), MutableStateFlow<T> {

    override var value: T
        get() = channel.value as T
        set(value) {
            channel.offer(value)
        }

    private val channel = if (initial !== Null) {
        ConflatedBroadcastChannel<T>(initial as T)
    } else {
        ConflatedBroadcastChannel<T>()
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.emitAll(channel.asFlow().map { it as T })
    }

}