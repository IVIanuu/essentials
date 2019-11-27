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

fun PrefBoxFactory.boolean(
    name: String,
    defaultValue: suspend () -> Boolean = { false }
) = box(name = name, defaultValue = defaultValue, serializer = BooleanSerializer)

private object BooleanSerializer : DiskBox.Serializer<Boolean> {
    override suspend fun deserialize(serialized: String) = serialized.toBoolean()
    override suspend fun serialize(value: Boolean) = value.toString()
}

fun PrefBoxFactory.double(
    name: String,
    defaultValue: suspend () -> Double = { 0.0 }
) = box(name = name, defaultValue = defaultValue, serializer = DoubleSerializer)

private object DoubleSerializer : DiskBox.Serializer<Double> {
    override suspend fun deserialize(serialized: String) = serialized.toDouble()
    override suspend fun serialize(value: Double) = value.toString()
}

fun PrefBoxFactory.float(
    name: String,
    defaultValue: suspend () -> Float = { 0f }
) = box(name = name, defaultValue = defaultValue, serializer = FloatSerializer)

private object FloatSerializer : DiskBox.Serializer<Float> {
    override suspend fun deserialize(serialized: String) = serialized.toFloat()
    override suspend fun serialize(value: Float) = value.toString()
}

fun PrefBoxFactory.int(
    name: String,
    defaultValue: suspend () -> Int = { 0 }
) = box(name = name, defaultValue = defaultValue, serializer = IntSerializer)

private object IntSerializer : DiskBox.Serializer<Int> {
    override suspend fun deserialize(serialized: String) = serialized.toInt()
    override suspend fun serialize(value: Int) = value.toString()
}

fun PrefBoxFactory.long(
    name: String,
    defaultValue: suspend () -> Long = { 0L }
) = box(name = name, defaultValue = defaultValue, serializer = LongSerializer)

private object LongSerializer : DiskBox.Serializer<Long> {
    override suspend fun deserialize(serialized: String) = serialized.toLong()
    override suspend fun serialize(value: Long) = value.toString()
}

fun PrefBoxFactory.string(
    name: String,
    defaultValue: suspend () -> String = { "" }
) = box(name = name, defaultValue = defaultValue, serializer = StringSerializer)

private object StringSerializer : DiskBox.Serializer<String> {
    override suspend fun deserialize(serialized: String) = serialized
    override suspend fun serialize(value: String) = value
}

fun PrefBoxFactory.stringSet(
    name: String,
    defaultValue: suspend () -> Set<String> = { emptySet() }
) = box(name = name, defaultValue = defaultValue, serializer = StringSetSerializer)

private object StringSetSerializer : DiskBox.Serializer<Set<String>> {
    override suspend fun deserialize(serialized: String) = serialized.split("=:=").toSet()
    override suspend fun serialize(value: Set<String>) = value.joinToString("=:=")
}