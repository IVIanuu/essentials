/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.injection

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.multibinding.MapBinding
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.getLazyMap
import com.ivianuu.injekt.multibinding.getMap
import com.ivianuu.injekt.multibinding.getProviderMap
import com.ivianuu.injekt.multibinding.injectLazyMap
import com.ivianuu.injekt.multibinding.injectMap
import com.ivianuu.injekt.multibinding.injectProviderMap
import kotlin.reflect.KClass

inline infix fun <reified T> BindingContext<T>.bindIntoClassMap(mapQualifier: Qualifier): BindingContext<T> =
    bindIntoMap(MapBinding(mapQualifier, T::class))

inline fun <reified T> BindingContext<T>.bindIntoClassMap(
    mapQualifier: Qualifier,
    override: Boolean = false
): BindingContext<T> = bindIntoMap(MapBinding(mapQualifier, T::class, override))

inline fun <reified T> Module.bindIntoClassMap(
    mapQualifier: Qualifier,
    override: Boolean = false,
    implementationQualifier: Qualifier? = null
) {
    bindIntoMap<T>(mapQualifier, T::class, override, implementationQualifier)
}

fun <T : Any> Component.getClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, T> = getMap(qualifier, parameters)

fun <T : Any> Component.getLazyClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, Lazy<T>> = getLazyMap(qualifier, parameters)

fun <T : Any> Component.getProviderClassMap(
    qualifier: Qualifier,
    defaultParameters: ParametersDefinition? = null
): Map<KClass<out T>, Provider<T>> = getProviderMap(qualifier, defaultParameters)

fun <T : Any> Component.injectClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, T>> = injectClassMap(qualifier, parameters)

fun <T : Any> Component.injectLazyClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Lazy<T>>> = injectLazyMap(qualifier, parameters)

fun <T : Any> Component.injectProviderClassMap(
    qualifier: Qualifier,
    defaultParameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Provider<T>>> = injectProviderMap(qualifier, defaultParameters)

fun <T : Any> InjektTrait.getClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, T> = getMap(qualifier, parameters)

fun <T : Any> InjektTrait.getLazyClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Map<KClass<out T>, Lazy<T>> = getLazyMap(qualifier, parameters)

fun <T : Any> InjektTrait.getProviderClassMap(
    qualifier: Qualifier,
    defaultParameters: ParametersDefinition? = null
): Map<KClass<out T>, Provider<T>> = getProviderMap(qualifier, defaultParameters)

fun <T : Any> InjektTrait.injectClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, T>> = injectMap(qualifier, parameters)

fun <T : Any> InjektTrait.injectLazyClassMap(
    qualifier: Qualifier,
    parameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Lazy<T>>> = injectLazyMap(qualifier, parameters)

fun <T : Any> InjektTrait.injectProviderClassMap(
    qualifier: Qualifier,
    defaultParameters: ParametersDefinition? = null
): Lazy<Map<KClass<out T>, Provider<T>>> = injectProviderMap(qualifier, defaultParameters)