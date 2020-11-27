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

package com.ivianuu.essentials.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, S> DataStore<T>.lens(
    get: (T) -> S,
    set: (T, S) -> T,
): DataStore<S> {
    val original = this
    return object : DataStore<S> {
        override val defaultData: S
            get() = get(original.defaultData)

        override val data: Flow<S>
            get() = original.data
                .map { get(it) }

        override suspend fun updateData(transform: suspend S.() -> S): S =
            get(original.updateData { set(this, transform(get(this))) })
    }
}
