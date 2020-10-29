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

import kotlinx.coroutines.flow.MutableStateFlow

// todo move to essentials-datastore-test
class TestDataStore<T>(override val defaultData: T) : DataStore<T> {
    override val data = MutableStateFlow(defaultData)

    var value by data::value

    private val _values = mutableListOf(defaultData)
    val values by this::_values

    override suspend fun updateData(transform: suspend (T) -> T): T {
        return transform(data.value)
            .also {
                data.value = it
                values += it
            }
    }
}
