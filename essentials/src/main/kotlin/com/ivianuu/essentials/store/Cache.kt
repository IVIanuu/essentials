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

import java.util.concurrent.atomic.AtomicReference

fun <T> Box<T>.asCached(): Box<T> = CacheBox(this)

internal class CacheBox<T>(private val wrapped: Box<T>) : Box<T> by wrapped {

    private val cachedValue = AtomicReference<Any?>(this)

    override suspend fun set(value: T) {
        cachedValue.set(value)
        wrapped.set(value)
    }

    override suspend fun get(): T? {
        var value = cachedValue.get()
        return if (value !== this) {
            value as T
        } else {
            value = wrapped.get()
            cachedValue.set(value)
            value
        }
    }

    override suspend fun delete() {
        cachedValue.set(this)
        wrapped.delete()
    }

}