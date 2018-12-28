package com.ivianuu.injekt

import kotlin.reflect.KClass

data class SetBinding<T : Any>(
    val type: KClass<T>,
    val name: String? = null
)

fun <T : Any> setBinding(type: KClass<T>, name: String? = null) =
    SetBinding(type, name)

inline fun <reified T : Any> setBinding(name: String? = null) =
    setBinding(T::class, name)

data class MapBinding<K : Any, T : Any>(
    val type: KClass<T>,
    val keyType: KClass<K>,
    val key: K,
    val name: String? = null
)

fun <K : Any, T : Any> mapBinding(
    type: KClass<T>,
    keyType: KClass<K>,
    key: K,
    name: String? = null
) = MapBinding(type, keyType, key, name)

inline fun <reified K : Any, reified T : Any> mapBinding(
    key: K,
    name: String? = null
) = mapBinding(T::class, K::class, key, name)

fun <T : Any> stringMapBinding(
    type: KClass<T>,
    key: String,
    name: String? = null
) = mapBinding(type, String::class, key, name)

inline fun <reified T : Any> stringMapBinding(
    key: String,
    name: String? = null
) = stringMapBinding(T::class, key, name)

fun <T : Any> classMapBinding(
    type: KClass<T>,
    key: KClass<*>,
    name: String? = null
) = mapBinding(type, KClass::class, key, name)

inline fun <reified T : Any> classMapBinding(
    key: KClass<*>,
    name: String? = null
) = classMapBinding(T::class, key, name)

fun <T : Any> intMapBinding(
    type: KClass<T>,
    key: Int,
    name: String? = null
) = mapBinding(type, Int::class, key, name)

inline fun <reified T : Any> intMapBinding(
    key: Int,
    name: String? = null
) = intMapBinding(T::class, key, name)

fun <T : Any> longMapBinding(
    type: KClass<T>,
    key: Long,
    name: String? = null
) = mapBinding(type, Long::class, key, name)

inline fun <reified T : Any> longMapBinding(
    key: Long,
    name: String? = null
) = longMapBinding(T::class, key, name)