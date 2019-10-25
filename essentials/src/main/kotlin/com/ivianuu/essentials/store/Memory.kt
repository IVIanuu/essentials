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

package com.ivianuu.essentials.store

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicReference

fun <T> NullableBox(initialValue: T? = null): Box<T?> = MemoryBox(initialValue)

fun <T> MemoryBox(initialValue: T): Box<T> = MemoryBoxImpl(initialValue)

internal class MemoryBoxImpl<T>(initialValue: T) : Box<T> {

    private val value = AtomicReference(initialValue)
    private val channel = BroadcastChannel<T>(1)

    override suspend fun set(value: T) {
        this.value.set(value)
        channel.offer(value)
    }

    override suspend fun get(): T = value.get()

    override suspend fun exists(): Boolean = true

    override suspend fun delete() {
    }

    override fun asFlow(): Flow<T> = flow {
        emit(get())
        channel
            .asFlow()
            .collect { emit(it) }
    }

}