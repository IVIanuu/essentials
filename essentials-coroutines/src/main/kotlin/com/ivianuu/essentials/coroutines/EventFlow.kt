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

import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

// todo replace with original once available

interface EventFlow<T> : Flow<T> {
    fun emit(value: T)
}

fun <T> EventFlow(bufferSize: Int = 0): EventFlow<T> = EventFlowImpl(bufferSize)

private class EventFlowImpl<T>(private val bufferSize: Int) : AbstractFlow<T>(), EventFlow<T> {

    private val collectors = mutableListOf<EventCollector>()
    private var bufferedValues: MutableList<T>? = null

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        val eventCollector = EventCollector(collector)
        try {
            val bufferedValues = synchronized(collectors) {
                collectors += eventCollector
                bufferedValues?.also { bufferedValues = null }
            }
            if (bufferedValues != null)
                eventCollector.emitBufferedValues(bufferedValues)
            eventCollector.collect()
        } finally {
            synchronized(collectors) {
                collectors -= eventCollector
            }
        }
    }

    override fun emit(value: T) {
        synchronized(collectors) {
            if (collectors.isNotEmpty()) {
                collectors.toList().forEach { it.emit(value) }
            } else if (bufferSize != 0) {
                val bufferedValues = bufferedValues ?: mutableListOf<T>()
                    .also { bufferedValues = it }
                bufferedValues += value
                while (bufferedValues.size > bufferSize) {
                    bufferedValues.removeAt(0)
                }
            }
        }
    }

    private inner class EventCollector(private val flowCollector: FlowCollector<T>) {
        private var valueAwaiter: Continuation<T>? = null

        suspend fun collect() {
            while (coroutineContext.isActive) {
                val value = suspendCancellableCoroutine<T> { cont ->
                    synchronized(this) { valueAwaiter = cont }
                }
                flowCollector.emit(value)
            }
        }

        suspend fun emitBufferedValues(values: List<T>) {
            values.forEach { flowCollector.emit(it) }
        }

        fun emit(value: T) {
            synchronized(this) { valueAwaiter?.also { valueAwaiter = null } }
                ?.resume(value)
        }
    }
}
