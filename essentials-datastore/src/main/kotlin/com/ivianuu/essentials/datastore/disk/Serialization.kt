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


import com.ivianuu.essentials.datastore.disk.Serializer
import com.ivianuu.essentials.datastore.disk.SerializerException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class JsonSerializer<T>(
    private val json: Json,
    private val serializer: KSerializer<T>,
) : Serializer<T> {
    override fun serialize(data: T): String {
        return try {
            json.encodeToString(serializer, data)
        } catch (e: Throwable) {
            throw SerializerException("Failed to serialize $data", e)
        }
    }

    override fun deserialize(serializedData: String): T {
        return try {
            json.decodeFromString(serializer, serializedData)
        } catch (e: Throwable) {
            throw SerializerException("Failed to deserialize $serializedData", e)
        }
    }
}

class JsonSerializerFactory(private val json: Json = Json) {
    inline fun <reified T> create() = create<T>(typeOf<T>())

    @Suppress("UNCHECKED_CAST")
    fun <T> create(type: KType): JsonSerializer<T> =
        JsonSerializer<T>(json, serializer(type) as KSerializer<T>)
}
