package com.ivianuu.injekt

import kotlin.reflect.KClass

data class SetBinding<T : Any>(
    val type: KClass<T>,
    val name: String? = null
)

inline fun <reified T : Any> setBinding(name: String? = null) = SetBinding(T::class, name)

data class MapBinding<K : Any, T : Any>(
    val type: KClass<T>,
    val keyType: KClass<K>,
    val key: K,
    val name: String? = null
)

inline fun <reified K : Any, reified T : Any> mapBinding(
    key: K,
    name: String? = null
) = MapBinding(T::class, K::class, key, name)

inline fun <reified T : Any> stringMapBinding(
    key: String,
    name: String? = null
) = mapBinding<String, T>(key, name)

inline fun <reified T : Any> classMapBinding(
    key: KClass<*>,
    name: String? = null
) = mapBinding<KClass<*>, T>(key, name)

inline fun <reified T : Any> intMapBinding(
    key: Int,
    name: String? = null
) = mapBinding<Int, T>(key, name)

inline fun <reified T : Any> longMapBinding(
    key: Long,
    name: String? = null
) = mapBinding<Long, T>(key, name)