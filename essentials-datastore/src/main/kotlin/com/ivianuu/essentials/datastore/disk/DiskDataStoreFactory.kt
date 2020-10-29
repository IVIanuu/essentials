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

package com.ivianuu.essentials.datastore.disk

import com.ivianuu.essentials.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import java.io.File

class DiskDataStoreFactory(
    private val scope: CoroutineScope,
    produceDataStoreDirectory: () -> File,
    lazySerializerFactory: () -> MoshiSerializerFactory,
) {

    @PublishedApi
    internal val serializerFactory by lazy(lazySerializerFactory)

    private val dataStoreDirectory by lazy(produceDataStoreDirectory)

    inline fun <reified T> create(
        name: String,
        noinline produceDefaultData: () -> T
    ): DataStore<T> = create(
        name = name,
        produceSerializer = { serializerFactory.create() },
        produceDefaultData = produceDefaultData
    )

    fun <T> create(
        name: String,
        produceSerializer: () -> Serializer<T>,
        produceDefaultData: () -> T
    ): DataStore<T> {
        return DiskDataStore(
            produceFile = { File(dataStoreDirectory, name) },
            produceSerializer = produceSerializer,
            produceDefaultData = produceDefaultData,
            scope = scope
        )
    }
}
