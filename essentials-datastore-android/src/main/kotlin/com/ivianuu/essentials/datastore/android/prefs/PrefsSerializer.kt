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

package com.ivianuu.essentials.datastore.android.prefs

import com.ivianuu.essentials.datastore.Serializer
import com.ivianuu.essentials.datastore.javaTypeOf
import com.ivianuu.injekt.ImplBinding
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@ImplBinding
class PrefsSerializer(moshi: Moshi) : Serializer<Prefs> {
    private val booleanAdapter = moshi.adapter(Boolean::class.java)
    private val doubleAdapter = moshi.adapter(Double::class.java)
    private val floatAdapter = moshi.adapter(Float::class.java)
    private val intAdapter = moshi.adapter(Int::class.java)
    private val longAdapter = moshi.adapter(Long::class.java)
    private val stringAdapter = moshi.adapter(String::class.java)
    private val stringSetAdapter = moshi.adapter<Set<String>>(javaTypeOf<Set<String>>())
    private val entryListAdapter = moshi.adapter<List<PrefsEntry>>(javaTypeOf<List<PrefsEntry>>())
    override fun serialize(data: Prefs): String {
        val entryList = data.asMap()
            .map { (key, value) ->
                val (serializedValue, type) = when (value) {
                    is Boolean -> booleanAdapter.toJson(value) to PrefsEntry.Type.BOOLEAN
                    is Double -> doubleAdapter.toJson(value) to PrefsEntry.Type.DOUBLE
                    is Float -> floatAdapter.toJson(value) to PrefsEntry.Type.FLOAT
                    is Int -> intAdapter.toJson(value) to PrefsEntry.Type.INT
                    is Long -> longAdapter.toJson(value) to PrefsEntry.Type.LONG
                    is String -> stringAdapter.toJson(value) to PrefsEntry.Type.STRING
                    is Set<*> -> stringSetAdapter.toJson(value as Set<String>) to PrefsEntry.Type.STRING_SET
                    else -> error("Unsupported type ${key.name}' $value")
                }
                PrefsEntry(
                    name = key.name,
                    value = serializedValue,
                    type = type
                )
            }
        return entryListAdapter.toJson(entryList)
            .also {
                println("serialized to $it")
            }
    }

    override fun deserialize(serializedData: String): Prefs {
        return Prefs(
            entryListAdapter.fromJson(serializedData)!!
                .map { (name, serializedValue, type) ->
                    Prefs.Key<Any>(name) to when (type) {
                        PrefsEntry.Type.BOOLEAN -> booleanAdapter.fromJson(serializedValue)!!
                        PrefsEntry.Type.DOUBLE -> doubleAdapter.fromJson(serializedValue)!!
                        PrefsEntry.Type.FLOAT -> floatAdapter.fromJson(serializedValue)!!
                        PrefsEntry.Type.INT -> intAdapter.fromJson(serializedValue)!!
                        PrefsEntry.Type.LONG -> longAdapter.fromJson(serializedValue)!!
                        PrefsEntry.Type.STRING -> stringAdapter.fromJson(serializedValue)!!
                        PrefsEntry.Type.STRING_SET -> stringSetAdapter.fromJson(serializedValue)!!
                    }
                }
                .toMap()
        ).also { println("deserialized to $it") }
    }
}

@JsonClass(generateAdapter = true)
internal data class PrefsEntry(
    @Json(name = "name") val name: String,
    @Json(name = "value") val value: String,
    @Json(name = "type") val type: Type
) {
    enum class Type {
        BOOLEAN,
        DOUBLE,
        FLOAT,
        INT,
        LONG,
        STRING,
        STRING_SET
    }
}
