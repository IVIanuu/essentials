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
import kotlin.time.Duration

fun PrefBoxFactory.boolean(
    name: String,
    defaultValue: Boolean = false
) = box(name = name, defaultValue = defaultValue, serializer = BooleanSerializer)

private object BooleanSerializer : DiskBox.Serializer<Boolean> {
    override fun deserialize(serialized: String) = serialized.toBoolean()
    override fun serialize(value: Boolean) = value.toString()
}

fun PrefBoxFactory.double(
    name: String,
    defaultValue: Double = 0.0
) = box(name = name, defaultValue = defaultValue, serializer = DoubleSerializer)

private object DoubleSerializer : DiskBox.Serializer<Double> {
    override fun deserialize(serialized: String) = serialized.toDouble()
    override fun serialize(value: Double) = value.toString()
}

fun PrefBoxFactory.float(
    name: String,
    defaultValue: Float = 0f
) = box(name = name, defaultValue = defaultValue, serializer = FloatSerializer)

private object FloatSerializer : DiskBox.Serializer<Float> {
    override fun deserialize(serialized: String) = serialized.toFloat()
    override fun serialize(value: Float) = value.toString()
}

fun PrefBoxFactory.int(
    name: String,
    defaultValue: Int = 0
) = box(name = name, defaultValue = defaultValue, serializer = IntSerializer)

private object IntSerializer : DiskBox.Serializer<Int> {
    override fun deserialize(serialized: String) = serialized.toInt()
    override fun serialize(value: Int) = value.toString()
}

fun PrefBoxFactory.long(
    name: String,
    defaultValue: Long = 0L
) = box(name = name, defaultValue = defaultValue, serializer = LongSerializer)

private object LongSerializer : DiskBox.Serializer<Long> {
    override fun deserialize(serialized: String) = serialized.toLong()
    override fun serialize(value: Long) = value.toString()
}

fun PrefBoxFactory.string(
    name: String,
    defaultValue: String = ""
) = box(name = name, defaultValue = defaultValue, serializer = StringSerializer)

private object StringSerializer : DiskBox.Serializer<String> {
    override fun deserialize(serialized: String) = serialized
    override fun serialize(value: String) = value
}

fun PrefBoxFactory.stringSet(
    name: String,
    defaultValue: Set<String> = emptySet()
) = box(name = name, defaultValue = defaultValue, serializer = StringSetSerializer)

private object StringSetSerializer : DiskBox.Serializer<Set<String>> {
    private const val VALUE_DELIMITER = "^\\"
    override fun deserialize(serialized: String): Set<String> {
        return if (serialized.isEmpty()) return emptySet()
        else serialized.split(VALUE_DELIMITER).toSet()
    }
    override fun serialize(value: Set<String>): String {
        return if (value.isEmpty()) ""
        else value.joinToString(VALUE_DELIMITER)
    }
}

fun PrefBoxFactory.stringList(
    name: String,
    defaultValue: List<String> = emptyList()
) = box(name = name, defaultValue = defaultValue, serializer = StringListSerializer)

private object StringListSerializer : DiskBox.Serializer<List<String>> {
    private const val VALUE_DELIMITER = "^\\"
    override fun deserialize(serialized: String): List<String> {
        return if (serialized.isEmpty()) return emptyList()
        else serialized.split(VALUE_DELIMITER)
    }
    override fun serialize(value: List<String>): String {
        return if (value.isEmpty()) ""
        else value.joinToString(VALUE_DELIMITER)
    }
}

fun PrefBoxFactory.stringMap(
    name: String,
    defaultValue: Map<String, String> = emptyMap()
) = box(name = name, defaultValue = defaultValue, serializer = StringMapSerializer)

private object StringMapSerializer : DiskBox.Serializer<Map<String, String>> {
    private const val ENTRY_DELIMITER = "^]"
    private const val VALUE_DELIMITER = "^\\"
    override fun deserialize(serialized: String): Map<String, String> {
        return if (serialized.isEmpty()) emptyMap()
        else serialized.split(ENTRY_DELIMITER)
            .map { it.split(VALUE_DELIMITER) }
            .associateBy { it[0] }
            .mapValues { it.value[1] }
    }

    override fun serialize(value: Map<String, String>): String {
        return if (value.isEmpty()) ""
        else value.entries
            .joinToString(ENTRY_DELIMITER) { "${it.key}$VALUE_DELIMITER${it.value}" }
    }
}

fun PrefBoxFactory.duration(
    name: String,
    defaultValue: Duration
) = box(name = name, defaultValue = defaultValue, serializer = DurationSerializer)

private object DurationSerializer : DiskBox.Serializer<Duration> {
    override fun deserialize(serialized: String): Duration {
        return Duration::class.java.getDeclaredConstructor(Double::class.java)
            .also { it.isAccessible = true }
            .newInstance(serialized.toDouble())
    }

    override fun serialize(value: Duration): String {
        return value.javaClass.declaredFields
            .first { it.type == Double::class.java }
            .also { it.isAccessible = true }
            .get(value)!!
            .toString()
    }
}