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

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class MoshiSerializer<T>(private val adapter: JsonAdapter<T>) : Serializer<T> {
    override fun serialize(data: T): String {
        return try {
            adapter.toJson(data)
        } catch (t: Throwable) {
            throw SerializerException("Failed to serialize $data", t)
        }
    }

    override fun deserialize(serializedData: String): T {
        return try {
            adapter.fromJson(serializedData)!!
        } catch (t: Throwable) {
            throw SerializerException("Failed to deserialize $serializedData", t)
        }
    }
}

class MoshiSerializerFactory(private val moshi: Moshi) {

    inline fun <reified T> create(): MoshiSerializer<T> =
        create(javaTypeOf<T>())

    fun <T> create(type: Type): MoshiSerializer<T> = MoshiSerializer(moshi.adapter(type))
}
