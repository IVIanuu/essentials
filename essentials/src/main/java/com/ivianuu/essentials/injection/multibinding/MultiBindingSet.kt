package com.ivianuu.essentials.injection.multibinding

import com.ivianuu.injekt.BeanDefinition
import com.ivianuu.injekt.ParamsDefinition
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.provider

/**
 * Wraps a [Set] of [BeanDefinition]s
 */
data class MultiBindingSet<T : Any>(val set: Set<BeanDefinition<T>>)

/**
 * Returns a [Set] of [T]s
 */
fun <T : Any> MultiBindingSet<T>.toSet(params: ParamsDefinition? = null): Set<T> =
    set.map { it.resolveInstance(params = params) }.toSet()

/**
 * Returns a [Set] of [Lazy]s of [T]
 */
fun <T : Any> MultiBindingSet<T>.toLazySet(params: ParamsDefinition? = null): Set<Lazy<T>> =
    set.map { lazy { it.resolveInstance(params = params) } }.toSet()

/**
 * Returns a [Set] of [Provider]s of [T]
 */
fun <T : Any> MultiBindingSet<T>.toProviderSet(defaultParams: ParamsDefinition? = null): Set<Provider<T>> =
    set.map { dec -> provider { dec.resolveInstance(it ?: defaultParams) } }.toSet()
