package com.ivianuu.essentials.injection.multibinding

import com.ivianuu.injekt.Declaration
import com.ivianuu.injekt.ParamsDefinition
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.provider
import kotlin.reflect.KClass

typealias ClassMultiBindingMap<T> = MultiBindingMap<KClass<out T>, T>
typealias IntMultiBindingMap<T> = MultiBindingMap<Int, T>
typealias LongMultiBindingMap<T> = MultiBindingMap<Long, T>
typealias StringMultiBindingMap<T> = MultiBindingMap<String, T>

/**
 * Wraps a [Set] of [Declaration]s
 */
data class MultiBindingSet<T : Any>(val set: Set<Declaration<T>>)

/**
 * Returns a [Set] of [T]s
 */
fun <T : Any> MultiBindingSet<T>.toSet(params: ParamsDefinition? = null): Set<T> =
    set.map { it.resolveInstance(params = params) }.toSet()

/**
 * Returns a [Set] of [Provider]s of [T]
 */
fun <T : Any> MultiBindingSet<T>.toProviderSet(defaultParams: ParamsDefinition? = null): Set<Provider<T>> =
    set.map { dec -> provider { dec.resolveInstance(it ?: defaultParams) } }.toSet()

/**
 * Returns a [Set] of [Lazy]s of [T]
 */
fun <T : Any> MultiBindingSet<T>.toLazySet(params: ParamsDefinition? = null): Set<Lazy<T>> =
    set.map { lazy { it.resolveInstance(params = params) } }.toSet()

/**
 * Wraps a [Map] of [Declaration]s
 */
data class MultiBindingMap<K : Any, T : Any>(val map: Map<K, Declaration<T>>)

/**
 * Returns a [Map] of [K] and [T]s
 */
fun <K : Any, T : Any> MultiBindingMap<K, T>.toMap(params: ParamsDefinition? = null) =
    map.mapValues { it.value.resolveInstance(params = params) }

/**
 * Returns a [Map] of [K] and [Provider]s of [T]
 */
fun <K : Any, T : Any> MultiBindingMap<K, T>.toProviderMap(defaultParams: ParamsDefinition? = null) =
    map.mapValues { dec -> provider { dec.value.resolveInstance(it ?: defaultParams) } }

/**
 * Returns a [Map] of [K] and [Lazy]s of [T]
 */
fun <K : Any, T : Any> MultiBindingMap<K, T>.toLazyMap(params: ParamsDefinition? = null) =
    map.mapValues { lazy { it.value.resolveInstance(params = params) } }