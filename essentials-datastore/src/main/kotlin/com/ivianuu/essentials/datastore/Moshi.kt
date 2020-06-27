package com.ivianuu.essentials.datastore

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class MoshiSerializer<T>(private val adapter: JsonAdapter<T>) : Serializer<T> {
    override fun serialize(data: T): String {
        return try {
            adapter.toJson(data)
        } catch (e: Exception) {
            throw SerializerException("Failed to serialize $data", e)
        }
    }

    override fun deserialize(serializedData: String): T {
        return try {
            adapter.fromJson(serializedData)!!
        } catch (e: Exception) {
            throw SerializerException("Failed to deserialize $serializedData", e)
        }
    }
}

class MoshiSerializerFactory(private val moshi: Moshi) {

    inline fun <reified T> create(): MoshiSerializer<T> =
        create(javaTypeOf<T>())

    fun <T> create(type: Type): MoshiSerializer<T> = MoshiSerializer(moshi.adapter(type))

}
