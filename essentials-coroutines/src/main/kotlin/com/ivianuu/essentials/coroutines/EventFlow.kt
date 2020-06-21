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

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll

// todo replace with original once available

interface EventFlow<T> : Flow<T> {
    fun offer(value: T)
}

fun <T> EventFlow(): EventFlow<T> =
    EventFlowImpl()

private class EventFlowImpl<T> : AbstractFlow<T>(),
    EventFlow<T> {

    private val channel = BroadcastChannel<T>(1)

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.emitAll(channel.asFlow())
    }

    override fun offer(value: T) {
        channel.offer(value)
    }
}
