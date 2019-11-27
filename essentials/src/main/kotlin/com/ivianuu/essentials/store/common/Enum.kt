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

package com.ivianuu.essentials.store.common

import com.ivianuu.essentials.store.DiskBox
import kotlin.reflect.KClass

inline fun <reified T : Enum<T>> PrefBoxFactory.enum(
    key: String,
    noinline defaultValue: suspend () -> T
) = enum(key, defaultValue, T::class)

fun <T : Enum<T>> PrefBoxFactory.enum(
    key: String,
    defaultValue: suspend () -> T,
    clazz: KClass<T>
) = box(key, defaultValue, EnumSerializer(clazz))

private class EnumSerializer<T : Enum<T>>(private val enumClass: KClass<T>) :
    DiskBox.Serializer<T> {
    override suspend fun deserialize(serialized: String): T =
        java.lang.Enum.valueOf(enumClass.java, serialized)

    override suspend fun serialize(value: T): String = value.name
}

inline fun <reified T> PrefBoxFactory.enumString(
    name: String,
    noinline defaultValue: suspend () -> T
) where T : Enum<T>, T : BoxValueHolder<String> =
    enumString(name, defaultValue, T::class)

fun <T> PrefBoxFactory.enumString(
    name: String,
    defaultValue: suspend () -> T,
    type: KClass<T>
) where T : Enum<T>, T : BoxValueHolder<String> =
    box(name, defaultValue, EnumStringPrefSerializer(type, defaultValue))

private class EnumStringPrefSerializer<T>(
    private val type: KClass<T>,
    private val defaultValue: suspend () -> T
) : DiskBox.Serializer<T> where T : Enum<T>, T : BoxValueHolder<String> {
    override suspend fun serialize(value: T) = value.value
    override suspend fun deserialize(serialized: String) = type.valueFor(serialized) {
        defaultValue()
    }
}

inline fun <reified T> PrefBoxFactory.enumStringSet(
    name: String,
    noinline defaultValue: suspend () -> Set<T> = { emptySet() }
) where T : Enum<T>, T : BoxValueHolder<String> =
    enumStringSet(name, defaultValue, T::class)

fun <T> PrefBoxFactory.enumStringSet(
    name: String,
    defaultValue: suspend () -> Set<T> = { emptySet() },
    type: KClass<T>
) where T : Enum<T>, T : BoxValueHolder<String> =
    box(name, defaultValue, EnumStringSetPrefAdapter(type))

private class EnumStringSetPrefAdapter<T>(
    private val type: KClass<T>
) : DiskBox.Serializer<Set<T>> where T : Enum<T>, T : BoxValueHolder<String> {
    override suspend fun serialize(value: Set<T>) = value.map { it.value }.joinToString("=:=")
    override suspend fun deserialize(serialized: String) =
        serialized.split("=:=").mapNotNull { type.valueForOrNull(it) }.toSet()
}