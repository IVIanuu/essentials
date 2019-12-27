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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

interface StateFlow<T> : Flow<T> {
    val isInitialized: Boolean
    val value: T
}

interface MutableStateFlow<T> : StateFlow<T> {
    override var value: T
}

fun <T> MutableStateFlow<T>.setIfChanged(value: T) {
    if (!isInitialized || this.value != value) this.value = value
}

fun <T> StateFlow(): MutableStateFlow<T> =
    DataFlowImpl(Null)

fun <T> StateFlow(initial: T): MutableStateFlow<T> =
    DataFlowImpl(initial)

private class DataFlowImpl<T>(
    initial: Any?
) : AbstractFlow<T>(), MutableStateFlow<T> {

    override val isInitialized: Boolean
        get() = channel.value !== Null

    override var value: T
        get() {
            val value = channel.value
            check(value !== Null)
            return value as T
        }
        set(value) {
            channel.offer(value)
        }

    private val channel: ConflatedBroadcastChannel<Any?> = if (initial !== Null) {
        ConflatedBroadcastChannel(initial as T)
    } else {
        ConflatedBroadcastChannel(Null)
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.emitAll(
            channel.asFlow()
                .filter { it !== Null }
                .map { it as T }        )
    }

}