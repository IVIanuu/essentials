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

package com.ivianuu.essentials.store.android.prefs

import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.DiskBox
import com.ivianuu.essentials.store.MoshiSerializerFactory
import com.ivianuu.essentials.store.Serializer
import com.ivianuu.essentials.store.SerializerException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class PrefBoxFactory(
    private val scope: CoroutineScope,
    private val producePrefsFile: () -> File,
    @PublishedApi internal val serializerFactory: MoshiSerializerFactory
) {

    private val boxes = ConcurrentHashMap<String, Box<*>>()

    private val prefs by lazy {
        DiskBox(
            produceFile = producePrefsFile,
            produceSerializer = { serializerFactory.create() },
            produceDefaultData = { Prefs() },
            scope = scope
        )
    }

    inline fun <reified T> create(
        key: String,
        noinline produceDefaultData: () -> T
    ): Box<T> = create(
        key = key,
        produceDefaultData = produceDefaultData,
        produceSerializer = { serializerFactory.create() }
    )

    fun <T> create(
        key: String,
        produceSerializer: () -> Serializer<T>,
        produceDefaultData: () -> T
    ): Box<T> {
        var box = boxes[key]
        if (box == null) {
            box = PrefBox(
                key = key,
                producePrefs = { prefs },
                produceSerializer = produceSerializer,
                produceDefaultData = produceDefaultData,
                scope = scope
            )
            boxes[key] = box
        }

        return box as Box<T>
    }
}

private class PrefBox<T>(
    private val key: String,
    producePrefs: () -> Box<Prefs>,
    produceSerializer: () -> Serializer<T>,
    produceDefaultData: () -> T,
    private val scope: CoroutineScope
) : Box<T> {

    override val defaultData by lazy(produceDefaultData)

    private val prefs by lazy(producePrefs)

    private val serializer by lazy(produceSerializer)

    override val data: Flow<T>
        get() = prefs.data
            .map {
                withContext(scope.coroutineContext) {
                    it.getOrDefault()
                }
            }
            .distinctUntilChanged()

    override suspend fun updateData(transform: suspend (T) -> T): T =
        withContext(scope.coroutineContext) {
            var newData: Any? = this@PrefBox
            prefs.updateData { prefs ->
                val currentData = prefs.getOrDefault()
                newData = transform(currentData)
                if (currentData == newData) prefs
                else Prefs(
                    prefs.map.toMutableMap().apply {
                        this[key] = (newData as T).serialize()
                    }
                )
            }
            newData as T
        }

    private fun T.serialize() = try {
        serializer.serialize(this)
    } catch (e: Exception) {
        throw SerializerException("Couldn't serialize value for key $key $this", e)
    }

    private fun String.deserialize() = try {
        serializer.deserialize(this)
    } catch (e: Exception) {
        throw SerializerException("Couldn't deserialize value for key '$key'", e)
    }

    private fun Prefs.getOrDefault(): T =
        if (map.containsKey(key)) map.getValue(key).deserialize() else defaultData

}
