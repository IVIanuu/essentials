package com.ivianuu.essentials.store.prefs

import com.ivianuu.essentials.store.DiskBox
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

inline fun <reified T> PrefBoxFactory.custom(
    name: String,
    defaultValue: T
) = custom(
    name = name,
    defaultValue = defaultValue,
    kSerializer = kotlinx.serialization.serializer()
)

fun <T> PrefBoxFactory.custom(
    name: String,
    defaultValue: T,
    kSerializer: KSerializer<T>
) = box(
    name = name,
    defaultValue = defaultValue,
    serializer = KotlinSerializationSerializer(kSerializer)
)

private class KotlinSerializationSerializer<T>(
    private val serializer: KSerializer<T>
) : DiskBox.Serializer<T> {
    override fun serialize(value: T): String = Json.stringify(serializer, value)
    override fun deserialize(serialized: String): T = Json.parse(serializer, serialized)
}
