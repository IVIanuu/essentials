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

import kotlinx.coroutines.flow.Flow

interface Box<T> {

    val defaultValue: T
    val isDisposed: Boolean

    suspend fun get(): T

    suspend fun set(value: T)

    suspend fun isSet(): Boolean

    suspend fun delete()

    fun asFlow(): Flow<T>

    fun dispose()
}

suspend fun <T> Box<T>.getOrNull(): T? = if (isSet()) get() else null

suspend inline fun <T> Box<T>.getOrElse(
    block: () -> T
): T = getOrNull() ?: block()

suspend inline fun <T> Box<T>.getOrSet(
    block: () -> T
): T = getOrNull() ?: block().also { set(it) }

suspend inline fun <T> Box<T>.update(reducer: T.() -> T) {
    set(get().reducer())
}