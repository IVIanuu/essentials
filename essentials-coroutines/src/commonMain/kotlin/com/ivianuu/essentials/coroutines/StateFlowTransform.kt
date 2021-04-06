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

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

fun <T, R> StateFlow<T>.map(transform: (T) -> R): StateFlow<R> = object : StateFlow<R> {
    override val replayCache: List<R>
        get() = this@map.replayCache.map(transform)
    override val value: R
        get() = transform(this@map.value)

    override suspend fun collect(collector: FlowCollector<R>) {
        this@map.collect {
            collector.emit(transform(it))
        }
    }
}

fun <T, R> MutableStateFlow<T>.lens(
    get: (T) -> R,
    set: (T, R) -> T,
): MutableStateFlow<R> = object : MutableStateFlow<R> {
    override val replayCache: List<R>
        get() = this@lens.replayCache.map(get)
    override var value: R
        get() = get(this@lens.value)
        set(value) {
            set(this@lens.value, value)
        }

    override suspend fun collect(collector: FlowCollector<R>) {
        this@lens.collect {
            collector.emit(get(it))
        }
    }

    override val subscriptionCount: StateFlow<Int>
        get() = this@lens.subscriptionCount

    override fun compareAndSet(expect: R, update: R): Boolean = this@lens.compareAndSet(
        set(this@lens.value, expect),
        set(this@lens.value, update)
    )

    override suspend fun emit(value: R) {
        this@lens.emit(set(this@lens.value, value))
    }

    override fun resetReplayCache() {
        this@lens.resetReplayCache()
    }

    override fun tryEmit(value: R): Boolean = this@lens.tryEmit(set(this@lens.value, value))
}
