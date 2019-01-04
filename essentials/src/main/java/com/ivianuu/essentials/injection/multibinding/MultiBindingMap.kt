package com.ivianuu.essentials.injection.multibinding

import com.ivianuu.injekt.BeanDefinition
import com.ivianuu.injekt.ParamsDefinition
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.provider
import kotlin.reflect.KClass

typealias ClassMultiBindingMap<T> = MultiBindingMap<KClass<out T>, T>
typealias IntMultiBindingMap<T> = MultiBindingMap<Int, T>
typealias LongMultiBindingMap<T> = MultiBindingMap<Long, T>
typealias StringMultiBindingMap<T> = MultiBindingMap<String, T>

/**
 * Wraps a [Map] of [BeanDefinition]s
 */
data class MultiBindingMap<K : Any, T : Any>(val map: Map<K, BeanDefinition<T>>)

/**
 * Returns a [Map] of [K] and [T]s
 */
fun <K : Any, T : Any> MultiBindingMap<K, T>.toMap(params: ParamsDefinition? = null) =
    map.mapValues { it.value.resolveInstance(params) }

/**
 * Returns a [Map] of [K] and [Lazy]s of [T]
 */
fun <K : Any, T : Any> MultiBindingMap<K, T>.toLazyMap(params: ParamsDefinition? = null) =
    map.mapValues { lazy { it.value.resolveInstance(params = params) } }

/**
 * Returns a [Map] of [K] and [Provider]s of [T]
 */
fun <K : Any, T : Any> MultiBindingMap<K, T>.toProviderMap(defaultParams: ParamsDefinition? = null) =
    map.mapValues { dec -> provider { dec.value.resolveInstance(it ?: defaultParams) } }