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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun <T> Box<T>.withDispatcher(
    dispatcher: CoroutineDispatcher
): Box<T> = DispatcherBox(dispatcher, this)

class DispatcherBox<T>(
    private val dispatcher: CoroutineDispatcher,
    private val wrapped: Box<T>
) : Box<T> by wrapped {

    override suspend fun get(): T? = withContext(dispatcher) { wrapped.get() }

    override suspend fun set(value: T) = withContext(dispatcher) { wrapped.set(value) }

    override suspend fun exists(): Boolean = withContext(dispatcher) { wrapped.exists() }

    override suspend fun delete() = withContext(dispatcher) { wrapped.delete() }

}