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

    suspend fun set(value: T)

    suspend fun get(): T

    suspend fun delete()

    suspend fun exists(): Boolean

    fun asFlow(): Flow<T>

}

suspend fun <T> Box<T>.getIfExists(): T? = if (exists()) get() else null

suspend inline fun <T> Box<T>.getOrDefault(defaultValue: () -> T): T =
    getIfExists() ?: defaultValue()

suspend inline fun <T> Box<T>.getOrSet(defaultValue: () -> T): T {
    return if (exists()) {
        get()
    } else {
        val value = defaultValue()
        set(value)
        value
    }
}