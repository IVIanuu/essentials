package com.ivianuu.essentials.store.android.prefs

import com.ivianuu.essentials.store.DiskBox
import com.squareup.moshi.JsonAdapter

class MoshiSerializer<T>(private val jsonAdapter: JsonAdapter<T>) : DiskBox.Serializer<T> {
    override fun serialize(value: T): String = jsonAdapter.toJson(value)
    override fun deserialize(serialized: String): T = jsonAdapter.fromJson(serialized) as T
}