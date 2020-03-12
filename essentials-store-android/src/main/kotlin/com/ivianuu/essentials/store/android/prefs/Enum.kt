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

package com.ivianuu.essentials.store.prefs

import com.ivianuu.essentials.store.DiskBox
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import kotlin.reflect.KClass

inline fun <reified T : Enum<T>> PrefBoxFactory.enum(
    key: String,
    defaultValue: T
) = enum(key, T::class, defaultValue)

fun <T : Enum<T>> PrefBoxFactory.enum(
    key: String,
    type: KClass<T>,
    defaultValue: T
) = box(
    key, defaultValue,
    EnumSerializer(type)
)

private class EnumSerializer<T : Enum<T>>(private val type: KClass<T>) :
    DiskBox.Serializer<T> {
    override fun deserialize(serialized: String): T =
        java.lang.Enum.valueOf(type.java, serialized)

    override fun serialize(value: T): String = value.name
}

inline fun <reified T : Enum<T>> PrefBoxFactory.enumSet(
    key: String,
    defaultValue: Set<T> = emptySet()
) = enumSet(key, T::class, defaultValue)

fun <T : Enum<T>> PrefBoxFactory.enumSet(
    key: String,
    type: KClass<T>,
    defaultValue: Set<T> = emptySet()
) = box(
    key, defaultValue,
    EnumSetSerializer(type)
)

private class EnumSetSerializer<T : Enum<T>>(private val type: KClass<T>) :
    DiskBox.Serializer<Set<T>> {
    override fun deserialize(serialized: String): Set<T> {
        return if (serialized.isEmpty()) emptySet() else serialized.split(VALUE_DELIMITER)
            .map { java.lang.Enum.valueOf(type.java, it) }
            .toSet()
    }

    override fun serialize(value: Set<T>): String = value.joinToString(VALUE_DELIMITER) { it.name }

    private companion object {
        private const val VALUE_DELIMITER = "^\\"
    }
}
