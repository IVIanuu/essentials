package com.ivianuu.essentials.store

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class MoshiSerializer<T>(private val adapter: JsonAdapter<T>) : Serializer<T> {
    override fun serialize(data: T): String = adapter.toJson(data)
    override fun deserialize(serialized: String): T = adapter.fromJson(serialized) as T
}

class MoshiSerializerFactory(private val moshi: Moshi) {

    inline fun <reified T> create(): MoshiSerializer<T> =
        create(javaTypeOf<T>())

    fun <T> create(type: Type): MoshiSerializer<T> = MoshiSerializer(moshi.adapter(type))

}
